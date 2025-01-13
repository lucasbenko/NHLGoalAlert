# NHL Goal Alert

![NHL Goal Alert Logo](/res/nhlgoalalert.png)

A simple Java program that uses the NHL and Govee APIs to make a goal horn go off and make lights flash when a goal is scored for the selected team.

Requires Java 17 or higher.

Instructions:
Modify the app.config.template, this is where the app gets all its settings from including goal delay and MAC Addresses. When done, rename the file to just app.config
This app was purpose built for my setup using Govee lights and smart plugs. It is setup to use 2 lights and 1 plug. If you require more or less, the program will require some modification.

Setting Goal Songs:
The goal song files are kept in the res/2023-24 folder. Place any .mp3 file here named in the format "Canucks.mp3" or "Golden_Knights.mp3", to use a shortened version of the horn name it "Canucks_Short.mp3", the app by default uses the short horn for home games, and the long horn for away games.

