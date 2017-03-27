#pragma once

#include <errno.h>
#include <sys/time.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/ethernet.h>
#include <signal.h>
#include <string.h>
#include <unistd.h>
#include <netinet/ip.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>
#include <netinet/ip_icmp.h>
#include <pthread.h> 
#include <fcntl.h>
#include <netdb.h>
#include <sys/mman.h>
#include <sys/ipc.h>
#include <sys/shm.h> 
#include <sys/ioctl.h>

#define LINK_PARENTLOST 0x80
#define UNSUPPORTED 	-1
#define MULTIPLAYER 	0
#define NORMAL8 		1
#define NORMAL32 		2
#define UART 			3
#define GP 				5

#define RFU_INIT 0
#define RFU_COMM 1
#define RFU_SEND 2
#define RFU_RECV 3

#define COMM_SIODATA32_L	0x120
#define COMM_SIODATA32_H	0x122
#define COMM_SIOCNT			0x128
#define COMM_SIODATA8		0x12a
#define COMM_SIOMLT_SEND 0x12a
#define COMM_SIOMULTI0 0x120
#define COMM_SIOMULTI1 0x122
#define COMM_SIOMULTI2 0x124
#define COMM_SIOMULTI3 0x126
#define COMM_RCNT			0x134

extern bool gba_link_enabled;

typedef struct {
	unsigned short int linkdata[4];
	unsigned short int linkcmd[4];
	unsigned short int numtransfers;
	int lastlinktime;
	unsigned char numgbas;
	unsigned char linkflags;
	int rfu_q[4];
	unsigned char rfu_request[4];
	int rfu_linktime[4];
	unsigned int rfu_bdata[4][7];
	unsigned int rfu_data[4][32];
} LINKDATA;

class lserver{
	int numbytes;
	fd_set fdset;
	timeval wsocktimeout;
	//timeval udptimeout;
	char inbuffer[256], outbuffer[256];
	int *intinbuffer;
	unsigned short *u16inbuffer;
	int *intoutbuffer;
	unsigned short *u16outbuffer;
	int counter;
	int done;
public:
	int howmanytimes;
	int tcpsocket[4];
	sockaddr_in udpaddr[4];
	lserver(void);
	int Init(void*);
	void Send(void);
	void Recv(void);
};

class lclient{
	fd_set fdset;
	timeval wsocktimeout;
	char inbuffer[256], outbuffer[256];
	int *intinbuffer;
	unsigned short *u16inbuffer;
	int *intoutbuffer;
	unsigned short *u16outbuffer;
	int numbytes;
public:
	bool oncesend;
	sockaddr_in serverinfo;
	int noblock;
	int numtransfers;
	lclient(void);
	int Init(struct hostent*, void*);
	void Send(void);
	void Recv(void);
	void CheckConn(void);
};

typedef struct {
	int tcpsocket;
	int numgbas;
	unsigned long thread;
	unsigned char type;
	unsigned char server;
	bool terminate;
	bool connected;
	bool speed;
	bool active;
} LANLINKDATA;

extern void LinkUpdate(void);
extern void LinkChildStop(void);
extern void LinkChildSend(unsigned short);
extern int openLinkLog(void);
extern void CloseLanLink(void);
extern bool InitLink(void);
extern void CloseLink(void);

extern LANLINKDATA lanlink;
extern int vbaid;
extern int linklog;
extern bool rfu_enabled;
extern int linktimeout;
extern lclient lc;
extern lserver ls;
extern int linkid;
extern int lspeed;
