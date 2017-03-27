// This file was written by denopqrihg for Windows
// Changes applied by Jacques-Olivier IMBERT
#include <string.h>
#include <malloc.h>
#include <stdio.h>
#include "Port.h"
#include "GBA.h"
#include "GBALink.h"
#ifdef ENABLE_NLS
	#include <libintl.h>
	#define _(x) gettext(x)
#else
	#define _(x) x
#endif

#define N_(x) x

int vbaid = 0;
bool gba_link_enabled = false;

#define UPDATE_REG(address, value) WRITE16LE(((u16 *)&ioMem[address]),value)

int linktime = 0;
u8 tspeed = 3;
u8 transfer = 0;
LINKDATA *linkmem = NULL;
int linkid = 0;
int lspeed = 0;
pthread_cond_t linksync[4];
int savedlinktime = 0;
char inifile[] = "vba1.ini";
int mmf=NULL;
char shm_handle[32];
int  shm_fd;
#define STATE_FILE "/vba.shared"
char linkevent[] = "VBA link event  ";
static int i, j;
int linktimeout = 1000;
int linklog = 0;
FILE *jjj = NULL;
LANLINKDATA lanlink;
u16 linkdata[4];
lserver ls;
lclient lc;
bool oncewait = false, after = false;
bool rfu_enabled = false;
u8 rfu_cmd, rfu_qsend, rfu_qrecv;
int rfu_state, rfu_polarity, rfu_counter, rfu_masterq;
int rfu_transfer_end;
u16 numtransfers = 0;
u32 rfu_masterdata[32];

extern unsigned char *gbMemory;
extern int gbInterrupt;

int trtimedata[4][4] = {
	{34080, 8520, 5680, 2840},
	{65536, 16384, 10923, 5461},
	{99609, 24903, 16602, 8301},
	{133692, 33423, 22282, 11141}
};

int trtimeend[3][4] = {
	{72527, 18132, 12088, 6044},
	{106608, 26652, 17768, 8884},
	{133692, 33423, 22282, 11141}
};

int gbtime = 1024;

int GetSIOMode(u16, u16);
void * LinkClientThread(void *);
void * LinkServerThread(void *);
int StartServer(void);

u16 StartRFU(u16);

unsigned long WaitForSingleObject(pthread_cond_t &tEvent, unsigned long ms)
	{
    int retcode=0;
    struct timeval now;
    struct timespec timeout;
    unsigned long usec;

    pthread_mutex_t tMut;
    pthread_mutex_init(&tMut, NULL);

    gettimeofday(&now, NULL);
    usec=(now.tv_usec+ms*1000);
    timeout.tv_sec=now.tv_sec+(unsigned long)(usec/1000000);
    timeout.tv_nsec=(usec%1000000)*1000;

    retcode=pthread_cond_timedwait(&tEvent, &tMut, &timeout);
    pthread_mutex_destroy(&tMut);

    if((retcode==ETIMEDOUT)||(retcode==EBUSY))
        {
		return retcode;
        }
    return EINTR;
	}

unsigned long GetLastError()
	{
	return(errno);
	} 

void StartLink(u16 value)
	{
	if (ioMem == NULL) return;

	if (rfu_enabled) 
		{
		UPDATE_REG(COMM_SIOCNT, StartRFU(value));
		return;
		}

	switch(GetSIOMode(value, READ16LE(&ioMem[0x134])))
		{
		case MULTIPLAYER:
			if(value & 0x80)
				{
				if(!linkid)
					{  
					if(!transfer)
						{
						if(lanlink.active)
							{
							if(lanlink.connected)
								{
								linkdata[0] = READ16LE(&ioMem[0x12a]);
								savedlinktime = linktime;
								tspeed = value & 3;
								ls.Send();
								transfer = 1;
								linktime = 0;
								UPDATE_REG(0x120, linkdata[0]);
								UPDATE_REG(0x122, 0xffff);
								WRITE32LE(&ioMem[0x124], 0xffffffff);
								if(lanlink.speed&&oncewait==false) ls.howmanytimes++;
								after = false;
								}
							} 
						else if(linkmem->numgbas>1)
							{
							linkmem->linkcmd[0] = ('M'<<8)+(value&3);
							linkmem->linkdata[0] = READ16LE(&ioMem[0x12a]);
							if(linkmem->numtransfers!=0) linkmem->lastlinktime = linktime;
							else linkmem->lastlinktime = 0;
							if((++linkmem->numtransfers)==0) linkmem->numtransfers=2;
							transfer = 1;
							linktime = 0;
							tspeed = value & 3;
							WRITE32LE(&ioMem[0x120], 0xffffffff);
							WRITE32LE(&ioMem[0x124], 0xffffffff);
							}	
						}
					}
				value &= 0xff7f;
				value |= (transfer!=0)<<7;
				}
			value &= 0xff8b;
			value |= (linkid ? 0xc : 8);
			value |= linkid<<4;
			UPDATE_REG(0x128, value);
			if(linkid) UPDATE_REG(0x134, 7);
			else UPDATE_REG(0x134, 3);
			break;
		case NORMAL8:
			if(linklog) fprintf(jjj, "Attempt to use 8-bit Normal mode %04x\n", value);
			UPDATE_REG(0x128, value);
			break;
		case NORMAL32:
			if(linklog) fprintf(jjj, "Attempt to use 32-bit Normal mode %04x %x%x\n", value, READ16LE(&ioMem[0x122]), READ16LE(&ioMem[0x120]));
			UPDATE_REG(0x128, value);
			break;
		case UART:
			if(linklog) fprintf(jjj, "Attempt to use UART mode %04x\n", value);
			UPDATE_REG(0x128, value);
			break;
		default:
			UPDATE_REG(0x128, value);
			break;
		}
}

void StartGPLink(u16 value)
	{
	UPDATE_REG(COMM_RCNT, value);

	if (!value)
		return;

	switch(GetSIOMode(READ16LE(&ioMem[0x128]), value)){
	case MULTIPLAYER:
		value &= 0xc0f0;
		value |= 3;
		if(linkid) value |= 4;
		UPDATE_REG(0x134, value);
		UPDATE_REG(0x128, ((READ16LE(&ioMem[0x128])&0xff8b)|(linkid ? 0xc : 8)|(linkid<<4)));
		return;
		break;
	case GP:
		if(linklog){ 
			if(value==0x8000) fprintf(jjj, "Circuit reset\n");
			else if(!rfu_enabled) fprintf(jjj, "Using General-purpose mode %04x\n", value);
		}
		if(value==0x8000) UPDATE_REG(0x134, value); //Circuit Reset
		else if (rfu_enabled) rfu_state = RFU_INIT;
		else //General Purpose Mode
			{
			UPDATE_REG(0x134, value);
			if(value==0x8055) value=0x8052;
			else if(value==0x8022) value=0x8025;
			else if(value==0x8050) value=0x8050;	
			else if(value==0x8020) value=0x8020;
			UPDATE_REG(0x134, value);
			}
		break;
	default:
		UPDATE_REG(0x134, value);
		break;
	}
	
	}


void LinkUpdate(int ticks)
{
	linktime += ticks;

	if (rfu_enabled)
	{
		rfu_transfer_end -= ticks;
		if (transfer && rfu_transfer_end <= 0) 
		{
			transfer = 0;
			if (READ16LE(&ioMem[COMM_SIOCNT]) & 0x4000)
			{
				IF |= 0x80;
				UPDATE_REG(0x202, IF);
			}
			UPDATE_REG(COMM_SIOCNT, READ16LE(&ioMem[COMM_SIOCNT]) & 0xff7f);
		}
		return;
	}

	if(lanlink.active){
		if(lanlink.connected){
			if(after){
				if(linkid&&linktime>6044){
					lc.Recv();
					oncewait = true;
				} else return;
			}
			if(linkid&&!transfer&&lc.numtransfers>0&&linktime>=savedlinktime){
				linkdata[linkid] = READ16LE(&ioMem[0x12a]);
				if(!lc.oncesend) lc.Send();
				lc.oncesend = false;
				UPDATE_REG(0x120, linkdata[0]);
				UPDATE_REG(0x128, READ16LE(&ioMem[0x128]) | 0x80);
				transfer = 1;
				if(lc.numtransfers==1) linktime = 0;
				else linktime -= savedlinktime;
			}
			if(transfer&&linktime>=trtimeend[lanlink.numgbas-1][tspeed]){
				if(READ16LE(&ioMem[0x128]) & 0x4000){
					IF |= 0x80;
					UPDATE_REG(0x202, IF);
				}
				UPDATE_REG(0x128, (READ16LE(&ioMem[0x128]) & 0xff0f) | (linkid << 4));
				transfer = 0;
				linktime -= trtimeend[lanlink.numgbas-1][tspeed];
				oncewait = false;
				if(!lanlink.speed){
					if(linkid) lc.Recv();
					else ls.Recv();
					UPDATE_REG(0x122, linkdata[1]);
					UPDATE_REG(0x124, linkdata[2]);
					UPDATE_REG(0x126, linkdata[3]);
					if(linklog) fprintf(jjj, "%04x %04x %04x %04x %10u\n", linkdata[0], linkdata[1], linkdata[2], linkdata[3], savedlinktime);
					oncewait = true;
				} else  {
					after = true;
					if(lanlink.numgbas==1){
						UPDATE_REG(0x122, linkdata[1]);
						UPDATE_REG(0x124, linkdata[2]);
						UPDATE_REG(0x126, linkdata[3]);
						if(linklog) fprintf(jjj, "%04x %04x %04x %04x %10u\n", linkdata[0], linkdata[1], linkdata[2], linkdata[3], savedlinktime);
					}

				}
			}
		}
		return;
	}
	
	if(linkid&&!transfer&&linktime>=linkmem->lastlinktime&&linkmem->numtransfers){
		linkmem->linkdata[linkid] = READ16LE(&ioMem[0x12a]);
		
		if(linkmem->numtransfers==1){
			linktime = 0;
			if(WaitForSingleObject(linksync[linkid], linktimeout)==ETIMEDOUT) linkmem->numtransfers=0;
		} else linktime -= linkmem->lastlinktime;
		
		switch((linkmem->linkcmd[0])>>8){
		case 'M':
			tspeed = (linkmem->linkcmd[0]) & 3;
			transfer = 1;
			WRITE32LE(&ioMem[0x120], 0xffffffff);
			WRITE32LE(&ioMem[0x124], 0xffffffff);
			UPDATE_REG(0x128, READ16LE(&ioMem[0x128]) | 0x80);
			break;
		}
	}
	
	if(!transfer) return;

	if(transfer&&linktime>=trtimedata[transfer-1][tspeed]&&transfer<=linkmem->numgbas){
		if(transfer-linkid==2){
			pthread_cond_signal(&linksync[linkid+1]);
			if(WaitForSingleObject(linksync[linkid], linktimeout)==ETIMEDOUT)
				linkmem->numtransfers=0;
			if(linklog)	fprintf(jjj, "%04x %04x %04x %04x %10u\n", 
				linkmem->linkdata[0], linkmem->linkdata[1], linkmem->linkdata[2], linkmem->linkdata[3], linkmem->lastlinktime);
		}
		
			
		UPDATE_REG(0x11e + (transfer<<1), linkmem->linkdata[transfer-1]);
		transfer++;
	}

	if(transfer&&linktime>=trtimeend[linkmem->numgbas-2][tspeed]){
		if(linkid==linkmem->numgbas-1){
			pthread_cond_signal(&linksync[0]);
			if(WaitForSingleObject(linksync[linkid], linktimeout)==ETIMEDOUT)
				linkmem->numtransfers=0;
			if(linklog)	fprintf(jjj, "%04x %04x %04x %04x %10u\n", 
				linkmem->linkdata[0], linkmem->linkdata[1], linkmem->linkdata[2], linkmem->linkdata[3], linkmem->lastlinktime);
		}
		transfer = 0;
		linktime -= trtimeend[0][tspeed];
		if(READ16LE(&ioMem[0x128]) & 0x4000){
			IF |= 0x80;
			UPDATE_REG(0x202, IF);
		}
		UPDATE_REG(0x128, (READ16LE(&ioMem[0x128]) & 0xff0f) | (linkid << 4));
		linkmem->linkdata[linkid] = 0xffff;
	}
	
	return;
}

inline int GetSIOMode(u16 siocnt, u16 rcnt)
{
	if (!(rcnt & 0x8000))
	{
		switch (siocnt & 0x3000) {
		case 0x0000: return NORMAL8;
		case 0x1000: return NORMAL32;
		case 0x2000: return MULTIPLAYER;
		case 0x3000: return UART;
		}
	}

	return GP;
}

u16 StartRFU(u16 value)
{	switch(GetSIOMode(value, READ16LE(&ioMem[0x134]))){
	case NORMAL8:
		rfu_polarity = 0;
		return value;
		break;
	case NORMAL32:
		if(value&8)	value &= 0xfffb;	// A kind of acknowledge procedure
		else value |= 4;
		if(value&0x80){
			if((value&3)==1) rfu_transfer_end = 2048;
			else rfu_transfer_end = 256;
			u16 a = READ16LE(&ioMem[0x122]);
			switch(rfu_state){
			case RFU_INIT:
				if(READ32LE(&ioMem[0x120])==0xb0bb8001){
					rfu_state = RFU_COMM;	// end of startup
				}
				UPDATE_REG(0x122, READ16LE(&ioMem[0x120]));
				UPDATE_REG(0x120, a);
				break;
			case RFU_COMM:
				if(a==0x9966){ 
					rfu_cmd = ioMem[0x120];
					if((rfu_qsend=ioMem[0x121])!=0){
						rfu_state = RFU_SEND;
						rfu_counter = 0;
					}
					if(rfu_cmd==0x25||rfu_cmd==0x24){
						linkmem->rfu_q[vbaid] = rfu_qsend;
					}
					UPDATE_REG(0x120, 0);
					UPDATE_REG(0x122, 0x8000);
				} else if(a==0x8000){
					switch(rfu_cmd){
					case 0x1a:	// check if someone joined
						if(linkmem->rfu_request[vbaid]!=0){
							rfu_state = RFU_RECV;
							rfu_qrecv = 1;
						}
						linkid = -1;
						rfu_cmd |= 0x80;
						break;
					case 0x1e:	// receive broadcast data
					case 0x1d:	// no visible difference
						rfu_polarity = 0;
						rfu_state = RFU_RECV;
						rfu_qrecv = 7;
						rfu_counter = 0;
						rfu_cmd |= 0x80;
						break;
					case 0x30:
						linkmem->rfu_request[vbaid] = 0;
						linkmem->rfu_q[vbaid] = 0;
						linkid = 0;
						numtransfers = 0;
						rfu_cmd |= 0x80;
						if(linkmem->numgbas==2) pthread_cond_signal(&linksync[1-vbaid]);
						break;
					case 0x11:	// ? always receives 0xff - I suspect it's something for 3+ players
					case 0x13:	// unknown
					case 0x20:	// this has something to do with 0x1f
					case 0x21:	// this too
						rfu_cmd |= 0x80;
						rfu_polarity = 0;
						rfu_state = 3;
						rfu_qrecv = 1;
						break;
					case 0x26:
						if(linkid>0){
							rfu_qrecv = rfu_masterq;
						}
						if((rfu_qrecv=linkmem->rfu_q[1-vbaid])!=0){
							rfu_state = RFU_RECV;
							rfu_counter = 0;
						}
						rfu_cmd |= 0x80;
						break;
					case 0x24:	// send data
						if((numtransfers++)==0) linktime = 1;
						linkmem->rfu_linktime[vbaid] = linktime;
						if(linkmem->numgbas==2){
							pthread_cond_signal(&linksync[1-vbaid]);
							WaitForSingleObject(linksync[vbaid], linktimeout);
						}
						rfu_cmd |= 0x80;
						linktime = 0;
						linkid = -1;
						break;
					case 0x25:	// send & wait for data
					case 0x1f:	// pick a server
					case 0x10:	// init
					case 0x16:	// send broadcast data
					case 0x17:	// setup or something ?
					case 0x27:	// wait for data ?
					case 0x3d:	// init
					default:
						rfu_cmd |= 0x80;
						break;
					case 0xa5:	//	2nd part of send&wait function 0x25
					case 0xa7:	//	2nd part of wait function 0x27
						if(linkid==-1){
							linkid++;
							linkmem->rfu_linktime[vbaid] = 0;
						}
						if(linkid&&linkmem->rfu_request[1-vbaid]==0){
							linkmem->rfu_q[1-vbaid] = 0;
							rfu_transfer_end = 256;
							rfu_polarity = 1;
							rfu_cmd = 0x29;
							linktime = 0;
							break;
						}
						if((numtransfers++)==0) linktime = 0;
						linkmem->rfu_linktime[vbaid] = linktime;
						if(linkmem->numgbas==2){
							if(!linkid||(linkid&&numtransfers)) pthread_cond_signal(&linksync[1-vbaid]);
							WaitForSingleObject(linksync[vbaid], linktimeout);
						}
						if(linkid>0){
							memcpy(rfu_masterdata, linkmem->rfu_data[1-vbaid], 128);
							rfu_masterq = linkmem->rfu_q[1-vbaid];
						}
						rfu_transfer_end = linkmem->rfu_linktime[1-vbaid] - linktime + 256;
						if(rfu_transfer_end<256) rfu_transfer_end = 256;
						linktime = -rfu_transfer_end;
						rfu_polarity = 1;
						rfu_cmd = 0x28;
						break;
					}
					UPDATE_REG(0x122, 0x9966);
					UPDATE_REG(0x120, (rfu_qrecv<<8) | rfu_cmd);
				} else {
					UPDATE_REG(0x120, 0);
					UPDATE_REG(0x122, 0x8000);
				}
				break;
			case RFU_SEND:
				if(--rfu_qsend==0) rfu_state = RFU_COMM;
				switch(rfu_cmd){
				case 0x16:
					linkmem->rfu_bdata[vbaid][rfu_counter++] = READ32LE(&ioMem[0x120]);
					break;
				case 0x17:
					linkid = 1;
					break;
				case 0x1f:
					linkmem->rfu_request[1-vbaid] = 1;
					break;
				case 0x24:
				case 0x25:
					linkmem->rfu_data[vbaid][rfu_counter++] = READ32LE(&ioMem[0x120]);
					break;
				}
				UPDATE_REG(0x120, 0);
				UPDATE_REG(0x122, 0x8000);
				break;
			case RFU_RECV:
				if(--rfu_qrecv==0) rfu_state = RFU_COMM;
				switch(rfu_cmd){
				case 0x9d:
				case 0x9e:
					if(rfu_counter==0){
						UPDATE_REG(0x120, 0x61f1);
						UPDATE_REG(0x122, 0);
						rfu_counter++;	
						break;
					}
					UPDATE_REG(0x120, linkmem->rfu_bdata[1-vbaid][rfu_counter-1]&0xffff);
					UPDATE_REG(0x122, linkmem->rfu_bdata[1-vbaid][rfu_counter-1]>>16);
					rfu_counter++;
					break;
				case 0xa6:
					if(linkid>0){
						UPDATE_REG(0x120, rfu_masterdata[rfu_counter]&0xffff);
						UPDATE_REG(0x122, rfu_masterdata[rfu_counter++]>>16);
					} else {					
						UPDATE_REG(0x120, linkmem->rfu_data[1-vbaid][rfu_counter]&0xffff);
						UPDATE_REG(0x122, linkmem->rfu_data[1-vbaid][rfu_counter++]>>16);
					}
					break;
				case 0x93:	// it seems like the game doesn't care about this value
					UPDATE_REG(0x120, 0x1234);	// put anything in here
					UPDATE_REG(0x122, 0x0200);	// also here, but it should be 0200
					break;
				case 0xa0:
				case 0xa1:
					UPDATE_REG(0x120, 0x641b);
					UPDATE_REG(0x122, 0x0000);
					break;
				case 0x9a:
					UPDATE_REG(0x120, 0x61f9);
					UPDATE_REG(0x122, 0);
					break;
				case 0x91:
					UPDATE_REG(0x120, 0x00ff);
					UPDATE_REG(0x122, 0x0000);
					break;
				default:
					UPDATE_REG(0x120, 0x0173);
					UPDATE_REG(0x122, 0x0000);
					break;
				}
				break;
			}	
			transfer = 1;
		}
		if(rfu_polarity) value ^= 4;	// sometimes it's the other way around
	default:
		return value;
	}
}

void gbLinkStart(u8 value){}


void gbLinkUpdate(void){}


bool InitLink()
{
	bool disable = true;

	linkid = 0;
	inifile[3]='1';
	
	if((lanlink.tcpsocket=socket(AF_INET, SOCK_STREAM, IPPROTO_TCP))==-1){
		printf("ERROR: Couldn't create socket.\n");
		return 0;
	}

	setsockopt(lanlink.tcpsocket, IPPROTO_TCP, TCP_NODELAY, (char*)&disable, sizeof(bool)); 
	
	if((shm_fd = shm_open(STATE_FILE,  (O_CREAT | O_EXCL | O_RDWR), (S_IRUSR | S_IWUSR))) > 0 ) {
		if((mmf = shm_open(STATE_FILE, (O_CREAT | O_RDWR), (S_IRUSR | S_IWUSR))) < 0)
			{
			close(lanlink.tcpsocket);
			printf("Error. Error creating file mapping.\n");
			return 0;			   
			}
		}
		
	else if((mmf = shm_open(STATE_FILE, (O_CREAT | O_RDWR), (S_IRUSR | S_IWUSR))) < 0){
		close(lanlink.tcpsocket);
		printf("Error. Error creating file mapping.\n");
		return 0;			   
	}
		
	if(GetLastError() == 183)
		vbaid = 1;
	else
 		vbaid = 0;

    ftruncate(mmf, sizeof(LINKDATA));
		  
	if((linkmem =  (LINKDATA*)mmap(0, sizeof(LINKDATA), (PROT_READ | PROT_WRITE), MAP_SHARED, mmf, 0)) == MAP_FAILED) {
		close(lanlink.tcpsocket);
		close(mmf);
		printf("ERROR: Error creating mapping file.\n");
		return 0;
	}

	if(linkmem->linkflags&LINK_PARENTLOST)
		vbaid = 0;

	if(vbaid==0){
		inifile[3]='1';
		linkid = 0;
		if(linkmem->linkflags&LINK_PARENTLOST){
			linkmem->numgbas++;
			linkmem->linkflags &= ~LINK_PARENTLOST;
		}
		else
			linkmem->numgbas=1;

		for(i=0;i<4;i++){
			linkevent[15]=(char)i+'1';
			if(pthread_cond_init(&linksync[i], NULL)!=0){
				close(lanlink.tcpsocket);
				munmap(linkmem, sizeof(LINKDATA));
				close(mmf);
				for(j=0;j<i;j++){
					pthread_cond_destroy(&linksync[j]);
				}
				printf("ERROR: Error opening event.\n");
				return 0;
			}
		}
	} else {
		vbaid=linkmem->numgbas;
		linkid = vbaid;
		linkmem->numgbas++;
		linklog = 0;
		if(linkmem->numgbas>4){
			linkmem->numgbas=4;
			close(lanlink.tcpsocket);
			printf("ERROR: Cannot connect 5 or more GBAs.\n");
			munmap(linkmem, sizeof(LINKDATA));
			close(mmf);
			return 0;
		}
		inifile[3]=(char)linkmem->numgbas+'0';
		for(i=0;i<4;i++){
			linkevent[15]=(char)i+'1';
			if(pthread_cond_init(&linksync[i],NULL)!=0){
				close(lanlink.tcpsocket);
				close(mmf);
				munmap(linkmem, sizeof(LINKDATA));
				for(j=0;j<i;j++){
					pthread_cond_destroy(&linksync[j]);
				}
				printf("ERROR: Error opening event.\n");
				return 0;
			}
		}
	}

	linkmem->lastlinktime=0xffffffff;
	linkmem->numtransfers=0;
	linkmem->linkflags=0;
	lanlink.connected = false;
	lanlink.thread = NULL;
	lanlink.speed = false;
	for(i=0;i<4;i++){
		linkmem->linkdata[i] = 0xffff;
		linkdata[i] = 0xffff;
	}
return 1;
}

int openLinkLog(void){
	char filename[20];
	if(linklog){
		sprintf(filename, "vbalog%1d.txt", vbaid+1);
		if((jjj=fopen(filename, "wt"))==NULL){
			return 0;
		}
		fprintf(jjj, "GBA0 GBA1 GBA2 GBA3 clocks between transfers\n");
	}
	return 1;
}

void CloseLink(void){
	if(lanlink.connected){
		if(linkid){
			char outbuffer[4];
			outbuffer[0] = 4;
			outbuffer[1] = -32;
			if(lanlink.type==0) send(lanlink.tcpsocket, outbuffer, 4, 0);
		} else {
			char outbuffer[12];
			int i;
			outbuffer[0] = 12;
			outbuffer[1] = -32;
			for(i=1;i<=lanlink.numgbas;i++){
				if(lanlink.type==0){
					send(ls.tcpsocket[i], outbuffer, 12, 0);
				} 
				close(ls.tcpsocket[i]);
			}
		}
	}
	linkmem->numgbas--;
	if(!linkid&&linkmem->numgbas!=0)
		linkmem->linkflags|=LINK_PARENTLOST;
	close(mmf);
	munmap(linkmem, sizeof(LINKDATA));

	for(i=0;i<4;i++){
		if(&linksync[i]!=NULL){
			pthread_cond_broadcast(&linksync[i]);
			pthread_cond_destroy(&linksync[i]);
		}
	}
	if(linklog) fclose(jjj);
	close(lanlink.tcpsocket);
return;
}

// Server
lserver::lserver(void){
	intinbuffer = (s32*)inbuffer;
	u16inbuffer = (u16*)inbuffer;
	intoutbuffer = (s32*)outbuffer;
	u16outbuffer = (u16*)outbuffer;
	oncewait = false;
}

int lserver::Init(void *serverdlg){

	sockaddr_in info;
	unsigned long nothing;
	char str[100];

	info.sin_family = AF_INET;
	info.sin_addr.s_addr = INADDR_ANY;
	info.sin_port = htons(6478);

	if(bind(lanlink.tcpsocket, (sockaddr *)&info, sizeof(sockaddr_in))==-1){
		close(lanlink.tcpsocket);
		if((lanlink.tcpsocket=socket(AF_INET, SOCK_STREAM, IPPROTO_TCP))==-1)
			return errno;
		if(bind(lanlink.tcpsocket, (sockaddr *)&info, sizeof(sockaddr_in))==-1)
			return errno;
	}

	if(listen(lanlink.tcpsocket, lanlink.numgbas)==-1)
		return errno;

	if(lanlink.thread!=NULL){
		lanlink.terminate = true;
		WaitForSingleObject(linksync[vbaid], 500);
		lanlink.thread = NULL;
	}
	lanlink.terminate = false;
	linkid = 0;
	
	gethostname(str, 100);
	printf("Server is ready! Server IP address is: %s\n", inet_ntoa(*(in_addr*)(gethostbyname(str)->h_addr_list[0])));

	if(pthread_create(&lanlink.thread, NULL, LinkServerThread, serverdlg)<0)
		{
		printf("Server Thread error\n");
		return (1);
		}
	return 0;

}

void * LinkServerThread(void *serverdlg){	
	fd_set fdset;
	timeval wsocktimeout;
	char inbuffer[256], outbuffer[256];
	int *intinbuffer = (int*)inbuffer;
	u16 *u16inbuffer = (u16*)inbuffer;
	int *intoutbuffer = (int*)outbuffer;
	u16 *u16outbuffer = (u16*)outbuffer;
	bool disable = true;

	wsocktimeout.tv_sec = 1;
	wsocktimeout.tv_usec = 0;
	i = 0;
	
	while(i<lanlink.numgbas){
		FD_ZERO(&fdset);
		FD_SET(lanlink.tcpsocket, &fdset);
		if(select(FD_SETSIZE, &fdset, NULL, NULL, &wsocktimeout)==1){
			if(lanlink.terminate){
				pthread_cond_signal(&linksync[vbaid]);
				return 0;
			}
			if((ls.tcpsocket[i+1]=accept(lanlink.tcpsocket, NULL, NULL))==-1){
				for(int j=1;j<i;j++) close(ls.tcpsocket[j]);
				printf("ERROR: Network error.\n");
				return 0;
			} else {
				setsockopt(ls.tcpsocket[i+1], IPPROTO_TCP, TCP_NODELAY, (char*)&disable, sizeof(bool));
				u16outbuffer[0] = i+1;
				u16outbuffer[1] = lanlink.numgbas;
				send(ls.tcpsocket[i+1], outbuffer, 4, 0);
				printf("Player %d connected.\n", i+1);
				i++;
			}
		}
	}
	printf("All players connected.\n");
			
	for(i=1;i<=lanlink.numgbas;i++){
		outbuffer[0] = 4;
		send(ls.tcpsocket[i], outbuffer, 4, 0);
	}

	lanlink.connected = true;

	return 0;
}

void lserver::Send(void){
	if(lanlink.type==0){	// TCP
		if(savedlinktime==-1){
			outbuffer[0] = 4;
			outbuffer[1] = -32;	//0xe0
			for(i=1;i<=lanlink.numgbas;i++){
				send(tcpsocket[i], outbuffer, 4, 0);
				recv(tcpsocket[i], inbuffer, 4, 0);
			}
		}
		outbuffer[1] = tspeed;
		u16outbuffer[1] = linkdata[0];
		intoutbuffer[1] = savedlinktime;
		if(lanlink.numgbas==1){
			if(lanlink.type==0){
				outbuffer[0] = 8;
				send(tcpsocket[1], outbuffer, 8, 0);
			}
		}
		else if(lanlink.numgbas==2){
			u16outbuffer[4] = linkdata[2];
			if(lanlink.type==0){
				outbuffer[0] = 10;
				send(tcpsocket[1], outbuffer, 10, 0);
				u16outbuffer[4] = linkdata[1];
				send(tcpsocket[2], outbuffer, 10, 0);
			}
		} else {
			if(lanlink.type==0){
				outbuffer[0] = 12;
				u16outbuffer[4] = linkdata[2];
				u16outbuffer[5] = linkdata[3];
				send(tcpsocket[1], outbuffer, 12, 0);
				u16outbuffer[4] = linkdata[1];
				send(tcpsocket[2], outbuffer, 12, 0);
				u16outbuffer[5] = linkdata[2];
				send(tcpsocket[3], outbuffer, 12, 0);
			}
		}
	}
	return;
}

void lserver::Recv(void){
	int numbytes;
	if(lanlink.type==0){	// TCP
		wsocktimeout.tv_sec = linktimeout / 1000;
		wsocktimeout.tv_usec =  linktimeout * 1000;
		for(i=0;i<lanlink.numgbas;i++) {FD_ZERO(&fdset); FD_SET(tcpsocket[i+1], &fdset);}
		if(select(FD_SETSIZE, &fdset, NULL, NULL, &wsocktimeout)==0){
			return;
		}
		howmanytimes++;
		for(i=0;i<lanlink.numgbas;i++){
			numbytes = 0;
			inbuffer[0] = 1;
			while(numbytes<howmanytimes*inbuffer[0])
				numbytes += recv(tcpsocket[i+1], inbuffer+numbytes, 256-numbytes, 0);
			if(howmanytimes>1) memcpy(inbuffer, inbuffer+inbuffer[0]*(howmanytimes-1), inbuffer[0]);
			if(inbuffer[1]==-32){
				char message[30];
				lanlink.connected = false;
				printf("Player %d disconnected.\n", i+2);
				outbuffer[0] = 4;
				outbuffer[1] = -32;
				for(i=1;i<lanlink.numgbas;i++){
					send(tcpsocket[i], outbuffer, 12, 0);
					recv(tcpsocket[i], inbuffer, 256, 0);
					close(tcpsocket[i]);
				}
				return;
			}
			linkdata[i+1] = u16inbuffer[1];
		}
		howmanytimes = 0;
	}
	after = false;
	return;
}


// Client
lclient::lclient(void){
	intinbuffer = (s32*)inbuffer;
	u16inbuffer = (u16*)inbuffer;
	intoutbuffer = (s32*)outbuffer;
	u16outbuffer = (u16*)outbuffer;
	numtransfers = 0;
	oncesend = false;
	return;
}

int lclient::Init(struct hostent* hostentry, void *waitdlg){
	unsigned long notblock = 1;
	unsigned long nothing;
	
	serverinfo.sin_family = AF_INET;
	serverinfo.sin_port = htons(6478);
	serverinfo.sin_addr = *((struct in_addr*)*hostentry->h_addr_list);
	
	if(fcntl(lanlink.tcpsocket, F_GETFL, &notblock)==-1)
		return errno;

	if(lanlink.thread!=NULL){
		lanlink.terminate = true;
		WaitForSingleObject(linksync[vbaid], 500);
		lanlink.thread = NULL;
	}

	printf("Connecting...\n");	
	lanlink.terminate = false;
	if(pthread_create((&lanlink.thread), NULL, (LinkClientThread), (waitdlg))<0)
		{
		printf("pthread_create error\n");
		return (1);
		}	
	return 0;
}

void* LinkClientThread(void *_cid){
	fd_set fdset;
	timeval wsocktimeout;
	int numbytes;
	char inbuffer[16];
	u16 *u16inbuffer = (u16*)inbuffer;
	unsigned long block = 0;

	if(connect(lanlink.tcpsocket, (sockaddr *)&lc.serverinfo, sizeof(sockaddr_in))==-1){
		if(errno!=EWOULDBLOCK){
			printf("Couldn't connect to server.\n");
			return 0;
		}
		wsocktimeout.tv_sec = 1;
		wsocktimeout.tv_usec = 0;
		do{
			if(lanlink.terminate) return 0;
			FD_ZERO(&fdset);
			FD_SET(lanlink.tcpsocket, &fdset);
		} while(select(FD_SETSIZE, NULL, &fdset, NULL, &wsocktimeout)!=1&&connect(lanlink.tcpsocket, (sockaddr *)&lc.serverinfo, sizeof(sockaddr_in))!=0);
	}
	
	fcntl(lanlink.tcpsocket, F_GETFL, &block);

	numbytes = 0;
	while(numbytes<4) 
		numbytes += recv(lanlink.tcpsocket, inbuffer+numbytes, 16, 0);
	linkid = (int)u16inbuffer[0];
	lanlink.numgbas = (int)u16inbuffer[1];
	
	printf("Connected as #%d\n", linkid+1);
	if(lanlink.numgbas!=linkid)	printf("Waiting for %d players to join\n", lanlink.numgbas-linkid);
	else printf("All players joined.\n");
		
	numbytes = 0;
	inbuffer[0] = 1;
	while(numbytes<inbuffer[0]) 
		numbytes += recv(lanlink.tcpsocket, inbuffer+numbytes, 16, 0);
		
	printf("Connected\n");

	block = 1;

	fcntl(lanlink.tcpsocket, F_GETFL, &block);

	lanlink.connected = true;
}

void lclient::CheckConn(void){
	if((numbytes=recv(lanlink.tcpsocket, inbuffer, 256, MSG_DONTWAIT))>0){
		while(numbytes<inbuffer[0]) 
			numbytes += recv(lanlink.tcpsocket, inbuffer+numbytes, 256, 0);
		if(inbuffer[1]==-32){
				outbuffer[0] = 4;
				send(lanlink.tcpsocket, outbuffer, 4, 0);
				lanlink.connected = false;
				printf("Server disconnected.\n");
				return;
		}
		numtransfers = 1;
		savedlinktime = 0;
		linkdata[0] = u16inbuffer[1];
		tspeed = inbuffer[1] & 3;
		for(i=1, numbytes=4;i<=lanlink.numgbas;i++)
			if(i!=linkid) linkdata[i] = u16inbuffer[numbytes++];
		after = false;
		oncewait = true;
		oncesend = true;
	}
	return;
}

void lclient::Recv(void){
	FD_ZERO(&fdset);
	FD_SET(lanlink.tcpsocket, &fdset);
	wsocktimeout.tv_sec = linktimeout / 1000;
	wsocktimeout.tv_usec =  linktimeout * 1000;
	if(select(FD_SETSIZE, &fdset, NULL, NULL, &wsocktimeout)==0){
		numtransfers = 0;
		return;
	}
	numbytes = 0;
	inbuffer[0] = 1;
	while(numbytes<inbuffer[0]) 
		numbytes += recv(lanlink.tcpsocket, inbuffer+numbytes, 256, 0);
	if(inbuffer[1]==-32){
		outbuffer[0] = 4;
		send(lanlink.tcpsocket, outbuffer, 4, 0);
		lanlink.connected = false;
		printf("Server disconnected.\n");
		return;
	}
	tspeed = inbuffer[1] & 3;
	linkdata[0] = u16inbuffer[1];
	savedlinktime = intinbuffer[1];
	for(i=1, numbytes=4;i<lanlink.numgbas+1;i++)
		if(i!=linkid) linkdata[i] = u16inbuffer[numbytes++];
	numtransfers++;
	if(numtransfers==0) numtransfers = 2;
	after = false;
}

void lclient::Send(){
	outbuffer[0] = 4;
	outbuffer[1] = linkid<<2;
	u16outbuffer[1] = linkdata[linkid];
	send(lanlink.tcpsocket, outbuffer, 4, 0);
	return;
}

void LinkSStop(void){
	if(!oncewait){
		if(linkid){
			if(lanlink.numgbas==1) return;
			lc.Recv();
		}
		else ls.Recv();
			
		oncewait = true;
		UPDATE_REG(0x122, linkdata[1]);
		UPDATE_REG(0x124, linkdata[2]);
		UPDATE_REG(0x126, linkdata[3]);
		if(linklog) fprintf(jjj, "%04x %04x %04x %04x %10u\n", linkdata[0], linkdata[1], linkdata[2], linkdata[3], savedlinktime);
	}
	return;
}

void LinkSSend(u16 value){
	if(linkid&&!lc.oncesend){
		linkdata[linkid] = value;
		lc.Send();
		lc.oncesend = true;
	}
}
