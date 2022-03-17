#ifndef __JOB_PROTOCOL_H__
#define __JOB_PROTOCOL_H__

#ifndef PORT
  #define PORT 50000
#endif

#ifndef MAX_JOBS
    #define MAX_JOBS 32
#endif

#include <sys/types.h>
// No paths or lines may be larger than the BUFSIZE below
#define BUFSIZE 256

// TODO: Add any extern variable declarations or struct declarations needed.
int connect_clients[FD_SETSIZE];
int listenfd;
struct sockaddr_in *self;

struct job{
    int job_pid;
    int watch[FD_SETSIZE];
};
int find_network_newline(const char *buf, int inbuf);
int check_command_valid(char *buf);
int check_command_server(char *buf);
int find_newline(const char *buf, int inbuf);
#endif
