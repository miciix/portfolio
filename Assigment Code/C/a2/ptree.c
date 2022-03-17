#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// Add your system includes here.
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>
#include "ptree.h"

// Defining the constants described in ptree.h
const unsigned int MAX_PATH_LENGTH = 1024;

// If TEST is defined (see the Makefile), will look in the tests
// directory for PIDs, instead of /proc.
#ifdef TEST
const char *PROC_ROOT = "tests";
#else
const char *PROC_ROOT = "/proc";
#endif


/*
 * Creates a PTree rooted at the process pid. The root of the tree is
 * placed in root. The function returns 0 if the tree was created
 * successfully and 1 if the tree could not be created or if at least
 * one PID was encountered that could not be found or was not an
 * executing process.
 */
char *get_name(pid_t pid){
    char path[MAX_PATH_LENGTH + 1];
    sprintf(path, "%s/%d/cmdline", PROC_ROOT, pid);
    FILE *cmdlinefile;
    char path_name[MAX_PATH_LENGTH + 1];
    char *name = malloc(MAX_PATH_LENGTH + 1);
    cmdlinefile = fopen(path,"r");
    if(fgets(path_name, MAX_PATH_LENGTH + 1, cmdlinefile) != NULL){
        char s[2] = "/";
        char *token;
        token = strtok(path_name, s);
        while( token != NULL )
        {
            strcpy(name, token);
            token = strtok(NULL, s);
        }
    }
    fclose(cmdlinefile);
    return name;
}

int exist_check(pid_t pid){
    char path[MAX_PATH_LENGTH + 1];
    struct stat pathbuf;
    sprintf(path, "%s/%d", PROC_ROOT, pid);
    if((lstat(path, &pathbuf)) != 0){
        return 1;
    }
    sprintf(path, "%s/%d/exe", PROC_ROOT, pid);
    if((lstat(path, &pathbuf)) != 0){
        return 1;
    }
    sprintf(path, "%s/%d/task/%d/children", PROC_ROOT, pid,pid);
    if((lstat(path, &pathbuf)) != 0){
        return 1;
    }
    sprintf(path, "%s/%d/cmdline", PROC_ROOT, pid);
    if((lstat(path, &pathbuf)) != 0){
        return 1;
    }
    return 0;
}
//struct TreeNode *buildsibling(pid_t pid)
struct TreeNode *buildnode(pid_t pid,int *error){
    FILE *childrenfile;
    int children_pid;
    char path[MAX_PATH_LENGTH + 1];
    struct TreeNode* current;
    struct TreeNode* sibling;
    struct TreeNode* new_child = malloc(sizeof(struct TreeNode));
    if (new_child == NULL) {
        *error = 1;
        return NULL;
    }
    current  = new_child;
    new_child -> pid = pid;
    if (strlen(get_name(pid)) > 0) {
        new_child -> name = get_name(pid);
    }else{
        new_child -> name = NULL;
    }
    new_child -> child_procs = NULL;
    new_child -> next_sibling = NULL;
    
    sprintf(path, "%s/%d/task/%d/children", PROC_ROOT, pid, pid);
    childrenfile = fopen(path,"r");

    while ((fscanf(childrenfile, "%d", &children_pid)) != EOF)
    {
        if (exist_check(children_pid) == 0) {
            if (current -> child_procs == NULL) {

                current -> child_procs = buildnode(children_pid, error);
            }else{

                if ((current -> child_procs ->next_sibling) == NULL) {
                    current -> child_procs ->next_sibling = buildnode(children_pid,error);
                    sibling = current -> child_procs ->next_sibling;
                }else{
                    sibling ->next_sibling = buildnode(children_pid,error);
                    sibling = sibling ->next_sibling;
                }

            }
        }else{
            *error = 1;
        }
    }
    fclose(childrenfile);
    return new_child;
}

int generate_ptree(struct TreeNode **root, pid_t pid) {
    // Here's a trick to generate the name of a file to open. Note
    // that it uses the PROC_ROOT variable
    int error = 0;
    int *error_ptr = &error;
    char *root_name;
    char path[MAX_PATH_LENGTH + 1];
    struct TreeNode *sibling;
    struct TreeNode *current;
    FILE *childrenfile;
    int children_pid;
    
    //valid check
    if (exist_check(pid) == 1) {
        error =  1;
        return 1;
    }
    
    struct TreeNode *roots = malloc(sizeof(struct TreeNode));
    if (roots == NULL) {
        error = 1;
        return 1;
    }
    current = roots;
    roots -> pid = pid;
    roots -> name = NULL;
    roots -> child_procs = NULL;
    roots -> next_sibling = NULL;
    
    //Processing name
    root_name = get_name(pid);
    if (strlen(root_name) > 0) {
        roots -> name = root_name;
    }
    
    //Processing Children
    sprintf(path, "%s/%d/task/%d/children", PROC_ROOT, current ->pid, current->pid);
    childrenfile = fopen(path,"r");
    while ((fscanf(childrenfile, "%d", &children_pid)) != EOF)
    {
        if (exist_check(children_pid) == 0) {
            if ((current -> child_procs) == NULL) {
                fscanf(childrenfile, "%d", &children_pid);
                current -> child_procs = buildnode(children_pid,error_ptr);
            }else{
                if ((current -> child_procs ->next_sibling) == NULL) {
                    current -> child_procs ->next_sibling = buildnode(children_pid, error_ptr);
                    sibling = current -> child_procs ->next_sibling;
                }else{
                    sibling ->next_sibling = buildnode(children_pid, error_ptr);
                    sibling = sibling ->next_sibling;
                }
            }
        }else{
            error = 1;
        }
    }
    
    *root = roots;
    if (error == 1) {
        return 1;
    }
    return 0;
}


/*
 * Prints the TreeNodes encountered on a preorder traversal of an PTree
 * to a specified maximum depth. If the maximum depth is 0, then the
 * entire tree is printed.
 */
void print_ptree(struct TreeNode *root, int max_depth) {
    // Here's a trick for remembering what depth (in the tree) you're at
    // and printing 2 * that many spaces at the beginning of the line.
    static int depth = 0;
    printf("%*s", depth * 2, "");
    
    // Your implementation goes here.
    if (root == NULL) {
        return ;
    }
    if ((root -> name) == NULL) {
        printf("%d\n",root -> pid);
    }else{
        printf("%d: %s\n",root -> pid, root -> name);
    }
    if (max_depth - 1 != depth) {
        if (root -> child_procs) {
            depth += 1;
            print_ptree(root -> child_procs, max_depth);
        }
    }
    if (root -> next_sibling) {
        print_ptree(root -> next_sibling, max_depth);
    }
    if (depth > 1) {
        depth -=1;
    }
}

