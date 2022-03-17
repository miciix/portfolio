#include <stdio.h>
#include <stdlib.h>


#include "ptree.h"


int main(int argc, char *argv[]) {
    // Creates a ptree to test printing
    struct TreeNode root, child_one, child_two, grandchild, grandchild2,grandchild3,grandchild4, grandchild5,grandchild6;
    root.pid = 0;
    root.name = "root process";
    root.child_procs = &child_one;
    root.next_sibling = NULL;
    
    child_one.pid = 30932;
    child_one.name = "30932";
    child_one.child_procs = &grandchild;
    child_one.next_sibling = &child_two;
    
    grandchild.pid = 30962;
    grandchild.name = "30962";
    grandchild.child_procs = &grandchild2;
    grandchild.next_sibling = NULL;
    
    grandchild2.pid = 30963;
    grandchild2.name = "30963";
    grandchild2.child_procs = NULL;
    grandchild2.next_sibling = &grandchild3;
    
    grandchild3.pid = 30964;
    grandchild3.name = "30964";
    grandchild3.child_procs = NULL;
    grandchild3.next_sibling = &grandchild4;
    
    grandchild4.pid = 30965;
    grandchild4.name = "30965";
    grandchild4.child_procs = NULL;
    grandchild4.next_sibling = NULL;
    
    child_two.pid = 31511;
    child_two.name = "31511";
    child_two.child_procs = &grandchild5;
    child_two.next_sibling = NULL;

    grandchild5.pid = 31547;
    grandchild5.name = "31547";
    grandchild5.child_procs = &grandchild6;
    grandchild5.next_sibling = NULL;
    
    grandchild6.pid = 31548;
    grandchild6.name = "31548";
    grandchild6.child_procs = NULL;
    grandchild6.next_sibling = NULL;

    print_ptree(&root, 0);

    return 0;
}

