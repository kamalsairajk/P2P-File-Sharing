#Computer Networks Course (CNT 5106C) Project
#P2P File Sharing
Implement BitTorrent protocol to construct a P2P file-sharing application in Java.
Distribute files with choking and unchoking mechanism between peers.
Establishing all operations using reliable protocol TCP.

Project Members: Project Group - 16

Kamal Sai Raj Kuncha  
Ipshita Aggarwal  
Aashish Dhawan 

The link to our demo video on onedrive@UF is: https://uflorida-my.sharepoint.com/:v:/g/personal/k_kuncha_ufl_edu/EZ4ZwgoX7f9FpDMA_dRAtYsBuh4q7bUIT_gxP1USku7-8w?e=Cq2Oj7

All requirements given in project description are working successfully.

Protocol:

In this project, the TCP protocol is used to establish a connection between peers that want to share files.
To transfer files, the peers must first send each other a handshake message that includes the header, zero bits, and peer ID.
Then a stream of data messages with message length, type, and payload is transmitted.

The payload has different kinds like piece and bitfield.There are different kinds of message like have, bitfield, choke, unchoke, interested, not interested, request, and piece that are used in communication between the peers.

Working:

The peers are started manually by on different cise servers in the order that is specified in the PeerInfo config file, and the peerProcess takes the peer ID as a parameter.
Every peer that is participating in file sharing and has started before it is supposed to build a TCP with the peer that just started.
All peers also read the common configuration file, which contains information about the file to be shared, including its size, choking and unchoking intervals, and desired neighbor count.
Bits 0 and 1 in the PeerInfo file indicate if a peer has the entire file. When a peer receives the entire file, the PeerInfo.cfg file is modified with bit 1 for that peer.
Because there are no additional peers to connect to, the first peer just listens on the port defined in the PeerInfo file.
We also keep track of when each peer establishes a TCP connection with another peer, when each of them  change their preferred neighbours or change their optimistically unchoked neighbour, when they are choked or unchoked by another peer or when each of them receive have/interested/not interested messages and when they finally finish downloading a piece or the entire file.

File Sharing:
If a peer requires a file, it searches for it using the filename or a keyword, as well as a hop count of one.

The search request is sent out in the overlay network to other peers within a hop count of the asking peer or fewer number of hops, and it expires after a pre-determined number of hop count seconds. The use of repeated search requests is not permitted.

When a peer with the required file receives a search request, it responds to the peer who initiated the request. If the peer who made the request receives the response, it takes it in; if not, it sends it to the peer who made the request.

If the requester receives a response, it accumulates all responses received until the request expires, and any responses received after that are simply not considered.

The peer that is requesting chooses the peer's response that matches the needed filename and piece index, and then starts a TCP connection with it. The peer thats requesting then copies the files from the sender peer to its own directory and makes the necessary modifications. The TCP connection is closed once the file is received.
If the search request was unsuccessful, the peer should restart the search with the hop count increased by one. It should keep growing the hop count until the search request is completed or the hop count reaches a certain threshold.

Terminating the Protocol
If the number of nodes exceeds the maximum number of hop counts, the nodes should be terminated. If on leaving the network  a node has just one neighbor, it simply terminates the TCP connection with that neighbor; otherwise, it chooses one of its neighbors to be the neighbor of all of its other neighbors (unless they are already neighbors). Then it should close any TCP connections and file transfers in progress.

Division of work:
We have divided our work based on the protocols being used in the whole project and related classes that are developed in the process. At a higher level the division is as follows:

Ipshita: 
handshake and bitfield protocols 
Actual message related classes and files
 
Aashish:
interested and not interested protocols
Choke and unchoke protocols 

Kamal:
request and piece protocols
Peer Process related classes and files

Remote peers related work was handled by Ipshita and Kamal. Any related work was completed in collaborations like for instance, logging since every protocol requires logging. Finally Video and report was done together as a team. 

Steps to run the project:
1. Login to CISE thunder remote server 
2. Go to CN_FINALPROJ_GRP16 folder -- Run "cd CN_FINALPROJ_GRP16"  
    This folder contains: 
            - The code required to run the project
            - The individual folders for all peers (in our project, we have taken 9 peers with information stated below; hence, 9 folders here from 1001-1009, two of which have the files to be transferred and the rest do not. If you intend to run the source files different to the ones provided to us, delete these folders
            - folder which consists files used and generated for the video, delete these if you see fit
           
3. Run "javac *.java"
4. Run "java peerProcess 100x" on different terminals for different servers logged into it.

So we tried testing to start all the peers at once but we were facing a lot of issues with ssh connection and key generation, even though we were able to come across this and able to run it successfully, the running of this turned unpredictable as few times we were able to run it completely whereas the other times we couldn't. So for the video we demonstrated using the manual logging into the server approach.

For running our project, we have used the following numbers:
- Number Of Preferred Neighbors = 3
- Unchoking Interval = 5 sec
- Optimistic Unchoking Interval = 10 sec
- File Name = demo.txt
- File Size = 24320576 bytes
- Piece Size = 173719 bytes

Peer Information:
- Peer IDs: 1001-1009
- Address of CISE remote macines: lin114-0x.cise.ufl.edu (x from numbers in range 0-11, for the 9 remote machines)
- Port number at every remote machine were in range(9601,9611).
- The peers that have the file already: 1001 and 1006
