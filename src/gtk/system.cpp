// VisualBoyAdvance - Nintendo Gameboy/GameboyAdvance (TM) emulator.
// Copyright (C) 1999-2003 Forgotten
// Copyright (C) 2004 Forgotten and the VBA development team

// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or(at your option)
// any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation,
// Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

#include <stdio.h>
#include <stdarg.h>
#include <string.h>

#include <SDL.h>
#include <SDL_thread.h>

#include "../GBA.h"
#include "../gb/GB.h"
#include "../gb/gbGlobals.h"
#include "../Util.h"
#include "../Sound.h"

#include "window.h"
#include "intl.h"

// Required vars, used by the emulator core
//
int  systemRedShift;
int  systemGreenShift;
int  systemBlueShift;
int  systemColorDepth;
int  systemDebug;
int  systemVerbose;
int  systemSaveUpdateCounter;
int  systemFrameSkip;
u32  systemColorMap32[0x10000];
u16  systemColorMap16[0x10000];
u16  systemGbPalette[24];
bool systemSoundOn;

int  emulating;
bool debugger;
int  RGB_LOW_BITS_MASK;

// Extra vars, only used for the GUI
//
int systemRenderedFrames;
int systemFPS;

// Sound stuff
//
const  int        sdlSoundSamples  = 2048;
const  int        sdlSoundAlign    = 4;
const  int        sdlSoundCapacity = sdlSoundSamples * 4;
const  int        sdlSoundTotalLen = sdlSoundCapacity + sdlSoundAlign;
static u8         sdlSoundBuffer[sdlSoundTotalLen];
static int        sdlSoundRPos;
static int        sdlSoundWPos;
static SDL_cond  *sdlSoundCond;
static SDL_mutex *sdlSoundMutex;

static inline int soundBufferFree()
{
  int ret = sdlSoundRPos - sdlSoundWPos - sdlSoundAlign;
  if (ret < 0)
    ret += sdlSoundTotalLen;
  return ret;
}

static inline int soundBufferUsed()
{
  int ret = sdlSoundWPos - sdlSoundRPos;
  if (ret < 0)
    ret += sdlSoundTotalLen;
  return ret;
}

inline VBA::Window * GUI()
{
  return VBA::Window::poGetInstance();
}

void systemMessage(int _iId, const char * _csFormat, ...)
{
  va_list args;
  va_start(args, _csFormat);

  GUI()->vPopupErrorV(_(_csFormat), args);

  va_end(args);
}

void systemDrawScreen()
{
  GUI()->vDrawScreen();
  systemRenderedFrames++;
}

bool systemReadJoypads()
{
  return true;
}

u32 systemReadJoypad(int)
{
  return GUI()->uiReadJoypad();
}

void systemShowSpeed(int _iSpeed)
{
  systemFPS = systemRenderedFrames;
  systemRenderedFrames = 0;

  GUI()->vShowSpeed(_iSpeed);
}

void system10Frames(int _iRate)
{
  GUI()->vComputeFrameskip(_iRate);
}

void systemFrame()
{
}

void systemSetTitle(const char * _csTitle)
{
  GUI()->set_title(_csTitle);
}

void systemScreenCapture(int _iNum)
{
  GUI()->vCaptureScreen(_iNum);
}

void systemWriteDataToSoundBuffer()
{
  if (SDL_GetAudioStatus() != SDL_AUDIO_PLAYING)
  {
    SDL_PauseAudio(0);
  }

  int       remain = soundBufferLen;
  const u8 *wave   = reinterpret_cast<const u8 *>(soundFinalWave);
  if (remain <= 0)
    return;
  SDL_mutexP(sdlSoundMutex);
  int n;
  while (remain >= (n = soundBufferFree())) {
    const int nAvail = ((sdlSoundTotalLen - sdlSoundWPos) + sdlSoundTotalLen) % sdlSoundTotalLen;
    if (n >= nAvail) {
      memcpy(&sdlSoundBuffer[sdlSoundWPos], wave, nAvail);
      sdlSoundWPos  = 0;
      wave       += nAvail;
      remain     -= nAvail;
      n          -= nAvail;
    }
    if (n > 0) {
      memcpy(&sdlSoundBuffer[sdlSoundWPos], wave, n);
      sdlSoundWPos = (sdlSoundWPos + n) % sdlSoundTotalLen;
      wave   += n;
      remain -= n;
    }
    if (!emulating || speedup || GUI()->iGetThrottle() != 0) {
      SDL_mutexV(sdlSoundMutex);
      return;
    }
    SDL_CondWait(sdlSoundCond, sdlSoundMutex);
  }
  const int nAvail = ((sdlSoundTotalLen - sdlSoundWPos) + sdlSoundTotalLen) % sdlSoundTotalLen;
  if (remain >= nAvail) {
    memcpy(&sdlSoundBuffer[sdlSoundWPos], wave, nAvail);
    sdlSoundWPos = 0;
    wave   += nAvail;
    remain -= nAvail;
  }
  if (remain > 0) {
    memcpy(&sdlSoundBuffer[sdlSoundWPos], wave, remain);
    sdlSoundWPos = (sdlSoundWPos + remain) % sdlSoundTotalLen;
  }
  SDL_mutexV(sdlSoundMutex);
}

static void soundCallback(void *, u8 *stream, int len)
{
  if (len <= 0 || !emulating)
    return;
  SDL_mutexP(sdlSoundMutex);
  const int nAvail = soundBufferUsed();
  if (len > nAvail)
    len = nAvail;
  const int nAvail2 = ((sdlSoundTotalLen - sdlSoundRPos) + sdlSoundTotalLen) % sdlSoundTotalLen;
  if (len >= nAvail2) {
    memcpy(stream, &sdlSoundBuffer[sdlSoundRPos], nAvail2);
    sdlSoundRPos = 0;
    stream += nAvail2;
    len    -= nAvail2;
  }
  if (len > 0) {
    memcpy(stream, &sdlSoundBuffer[sdlSoundRPos], len);
    sdlSoundRPos = (sdlSoundRPos + len) % sdlSoundTotalLen;
    stream += len;
  }
  SDL_CondSignal(sdlSoundCond);
  SDL_mutexV(sdlSoundMutex);
}

bool systemSoundInit()
{
  SDL_AudioSpec stAudio;

  switch (soundQuality)
  {
  case 1:
    stAudio.freq = 44100;
    soundBufferLen = 1470 * 2;
    break;
  case 2:
    stAudio.freq = 22050;
    soundBufferLen = 736 * 2;
    break;
  case 4:
    stAudio.freq = 11025;
    soundBufferLen = 368 * 2;
    break;
  }

  stAudio.format   = AUDIO_S16SYS;
  stAudio.channels = 2;
  stAudio.samples  = sdlSoundSamples;
  stAudio.callback = soundCallback;
  stAudio.userdata = NULL;

  if (SDL_OpenAudio(&stAudio, NULL) < 0)
  {
    fprintf(stderr, "Failed to open audio: %s\n", SDL_GetError());
    return false;
  }

  sdlSoundCond  = SDL_CreateCond();
  sdlSoundMutex = SDL_CreateMutex();

  soundBufferTotalLen = soundBufferLen * 10;
  sdlSoundRPos = sdlSoundWPos = 0;
  systemSoundOn = true;

  return true;
}

void systemSoundShutdown()
{
  SDL_mutexP(sdlSoundMutex);
  int iSave = emulating;
  emulating = 0;
  SDL_CondSignal(sdlSoundCond);
  SDL_mutexV(sdlSoundMutex);

  SDL_DestroyCond(sdlSoundCond);
  sdlSoundCond = NULL;

  SDL_DestroyMutex(sdlSoundMutex);
  sdlSoundMutex = NULL;

  SDL_CloseAudio();

  emulating = iSave;
  systemSoundOn = false;
}

void systemSoundPause()
{
  SDL_PauseAudio(1);
}

void systemSoundResume()
{
  SDL_PauseAudio(0);
}

void systemSoundReset()
{
}

u32 systemGetClock()
{
  return SDL_GetTicks();
}

void systemUpdateMotionSensor()
{
}

int systemGetSensorX()
{
  return 0;
}

int systemGetSensorY()
{
  return 0;
}

void systemGbPrint(u8 * _puiData,
                   int  _iPages,
                   int  _iFeed,
                   int  _iPalette,
                   int  _iContrast)
{
}

void systemScreenMessage(const char * _csMsg)
{
}

bool systemCanChangeSoundQuality()
{
  return true;
}

bool systemPauseOnFrame()
{
  return false;
}

void systemGbBorderOn()
{
}

void debuggerMain()
{
}

void debuggerSignal(int, int)
{
}

void debuggerOutput(char *, u32)
{
}

void debuggerBreakOnWrite(u32 address, u32 oldvalue, u32 value, int size, int t)
{
}

void (*dbgMain)() = debuggerMain;
void (*dbgSignal)(int, int) = debuggerSignal;
void (*dbgOutput)(char *, u32) = debuggerOutput;
