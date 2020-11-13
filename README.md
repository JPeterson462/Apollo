# Apollo
This is the Git repository for the 2D isometric multiplayer game Apollo. While the game is publicly available and is functional, it is probably not ready for begin played/run on public servers on the internet. The following are probably the biggest changes:
- Synchronizing over network latency
- Storing user data on a SQL server
- Implementing security features to do things like verifying the packet sender

However, the code is likely to remain in its current state. The code is far from perfect, but you are free to make any modifications either for your own edification or to create a marketable game, **as long as you attribute the original source code**, as well as any of the original graphics that you use.

The code is roughly structured as follows:
- Apollo/core: LibGDX client
  - net.digiturtle.apollo.graphics: Rendering code for matches
  - net.digiturtle.apollo.screens: UI logic and rendering code
- Apollo-common: Shared code, including events and match data structures
  - net.digiturtle.apollo.definitions: Data structure templates for matches
  - net.digiturtle.apollo.match: Match data structures
  - net.digiturtle.apollo.match.event: Event definitions and match simulators along with match server manager
  - net.digiturtle.apollo.networking: Networking utilities and Netty wrapper helper classes
- Apollo-match: Match server
- Apollo-manager: Manager server that handles user data and lobbies for match servers
