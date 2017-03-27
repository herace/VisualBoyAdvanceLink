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
#include <stdlib.h>
#include <string.h>

#include "../GBA.h"
#include "../Port.h"
#include "../armdis.h"
#include "../elf.h"
#include "../exprNode.h"

extern bool debugger;
extern int emulating;

extern struct EmulatedSystem emulator;

void debuggerBreakOnWrite(u32 *mem, u32 oldvalue, u32 value, int size)
{
  u32 address = 0;
  if(mem >= (u32*)&workRAM[0] && mem <= (u32*)&workRAM[0x3ffff])
    address = 0x2000000 + ((unsigned long)mem - (unsigned long)&workRAM[0]);
  else
    address = 0x3000000 + ((unsigned long)mem - (unsigned long)&internalRAM[0]);

  if(size == 2)
    printf("Breakpoint (on write) address %08x old:%08x new:%08x\n", 
           address, oldvalue, value);
  else if(size == 1)
    printf("Breakpoint (on write) address %08x old:%04x new:%04x\n", 
           address, (u16)oldvalue,(u16)value);
  else
    printf("Breakpoint (on write) address %08x old:%02x new:%02x\n", 
           address, (u8)oldvalue, (u8)value);
  debugger = true;
}

