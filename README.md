## Space Invaders

### Introduction

I have made the Space Invaders game as my second game. It uses a client-server architecture and is a single player game. I will give instructions on how to run the program and in the following sections explain certain parts of the game. 

### Running my program

There are two ways to run my Space Invaders game with scripting enabled:

1. **JAR**:
    1. Find the JAR file for this project in
    `[root_dir]\out\artifacts\space_invaders_jar\space-invaders.jar`.
    2. Open a command line and type `java -jar space-invaders.jar s` (for server).
    3. For running clients, open other command lines, type and execute `java -jar space-invaders.jar c` ONCE. This game is implemented using a client-server architecture but is a single-player game. So there can be only one client. Running more clients may have undefined results.
    4. Remember that you need the run the server first and then the client, otherwise this might throw some exception. This should be normal, as for most multiplayer games, the headless server generally is always running. 
    5. Also currently, the client searches for a running server in `localhost`, so running the server and client in different computers will not work. If you still want to run it in different computers, follow my second way of running the program and before building it, open `Constants.java` and assign the server's IP to the `SERVER_ADDRESS` String variable.
      
2. **IntelliJ**:
    1. Install [IntelliJ Community Edition](https://www.jetbrains.com/idea/download/#section=windows).
    2. Import and build my project.
    3. There should be two run configurations - one for the server and one for the client. Run the "Server" first and then the "Client". The shortcut for running programs in IntelliJ is `Alt + Shift + F10`.
    4. If you don't find the run configurations, make two yourself. For the sever, give a command line argument of `s` and for the client, give a command line argument of `c` (without the quotes).
    
### Controls

The controls are displayed on screen, but still I will give a description of the same here:

1. Press `A` to move left and `D` to move right.
2. Press `SPACE` to shoot bullets. 

### The game

This follows the classic game rules. I will list them and any variations here:

1. There are 9 rows and 9 columns of enemies starting from the top. In total there are 81 enemies to start with.
2. There are no additional enemies added in the course of the game.
3. The enemies keep going left, coming down, going right and then again coming down, and continues this process. Its velocity keeps increasing with time.
3. Once the player shoots an enemy, it's color changes to red. And it keeps getting darker if it takes in more bullets, and ultimately disappears when its completely killed. Basically there is a `health` factor for each enemy which gets reduced with incoming bullets.
4. The objective of the game is to shoot all enemies before they can reach the user and collide with it. 
5. If the player dies, the game restarts with the number of deaths incremented for the player. 
6. A score is maintained as is a live accuracy counter which is based on the number of bullets fired and the ones which actually hit an enemy.
7. The number of deaths reduce the increase rate of the score. 
8. The game is continuous, i.e. if you die, then it restarts and you keep building your score. 

### Client-server architecture

The server here creates the enemy map and sends it over to the client, i.e. it establishes the environment. From there on, the client handles the player movements and killing of enemies. 

### Scripting 

Scripting is enabled in my game and the script file lies in the same directory as my previous assignment section. In the script file I can change the color of my player to something else. I expose the player object to the script and then I call the `changeColor` method in that to change the color. 

### Difference and code reuse

From the initial assignment, I have tried to make by game engine as pluggable (modular) as possible. Thus when I show the difference report below, you will see that the engine part has hardly changed. I will explain whatever changes were done and it will become clear that the engine was very reusable indeed. Personally, it was incredibly easy to make the game with my engine. That being said, it is also true that I am making a 2D game with one level. With multiple levels, probably some change had to be done to my engine. 

I first compared my `src` directory from the `scripting` part of the assignment with the `src` of space-invaders. The former has the latest code for my engine which also the firt game/playground which I was building throughout the semester. While making the second game, I reused the previous engine and removed the game code to add new game code for space-invaders. Our goal is to see how much I could reuse my code from the first game to make my second game. 
 
 Here is a report for the `src` folder comparison. 
 
 ![Imgur](http://i.imgur.com/TOoZaml.png)
 
 The green lines show new files added, yellow shows removed files and purple shows changed files. You will see many differences here, but it is obvious that as I made a new game the **game code** itself will be different. That shows the files in the `characters` folder, `util` folder and the `GameEventHandler` class, all of which are specifically part of the game that I am building. Of interest to us is the `engine` folder, because that's where all my engine code lies. Let's see what has been changed there. Out of 17 files in engine, only 3 files have been changed for making my second game. They are:
  
1. **`EventManager`**: The change here is trivial. I had previously used a certain technique to mirror certain events for multiple clients in the recording event queue. In the new file, I removed this completely, as I don't need it anymore thus making it even more reusable.
   ![Imgur](http://i.imgur.com/s9isqDm.png)
2. **`EngineConstants`**: As I don't need replays here, I have simply disabled it, using a flag. Ideally this should be driven from the game as well, but for this assignment, I kept ot like this. 
   ![Imgur](http://i.imgur.com/lgd7pFH.png)
3. **`MainEngine`**: I did not add the scenarios where their might not be any scripting or replays involved, so I added a small check as follows:
   ![Imgur](http://i.imgur.com/kxEFNB7.png)
   This actually makes my engine more reusable from now on. Similarly: 
   ![Imgur](http://i.imgur.com/yE2up3U.png)
   An important change and probably a con of my engine is that I am printing the instructions for playing the game through the engine. This should ideally be handled by a callback registered by the controller and hence the game manager can do that. But due to time shortage, I added it to the engine. And that's this change:
   ![Imgur](http://i.imgur.com/6z0R4Wn.png)
   
And that's it! All the core game object code, network code, synchronization using CMB (yes, my code still uses CMB), replay, scripting, event management, everything has remained exactly the same as it was for the first game! I can conclude hence that my engine code, is very reusable. 

It should also be noted that I am not comparing game code here (engine and game code lie very separately in my implementations). This is because it does not make sense to compare something like `SpaceInvadersManager` and `SimpleRaceManager`. They will obviously be different because they have different characters, different gameplay, rules, etc. So I have only compared the engine part of tmy code to measure resuability. 

Full reports are available in the `reports` directory. The reports were taken using a trial version of Araxis Merge.
 
### Thoughts

All in all, it was very interesting to use my game engine and make a new game. And it was very a smooth process, the engine being very reusable. The game itself is a little difficult. I myself have not yet been able to kill all enemies in one go. Hope you like it!
 
### Screencast

I have uploaded a screencast to YouTube so that it's easier for you to check what I've done. I play the game one time and when I die, you can see that my deaths increase and the game restarts. I also show the usage of scripts in the end.

https://www.youtube.com/watch?v=p15XHEM9_8o&feature=youtu.be
 
 
