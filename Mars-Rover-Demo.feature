Feature: Mars Rover 
The Mars Rover is given an area to rove in, an initial position and direction, and a series of moves.   The rover outputs its new position.  
A rover’s position is represented by a combination of an x and y co-ordinates and a letter representing one of the four cardinal compass points. The plateau is divided up into a grid to simplify navigation. An example position might be 0, 0, N, which means the rover is in the bottom left corner and facing North.
In order to control a rover, NASA sends a simple string of letters. The possible letters are ‘L’, ‘R’ and ‘M’. ‘L’ and ‘R’ makes the rover spin 90 degrees left or right respectively, without moving from its current spot.
‘M’ means move forward one grid point, and maintain the same heading.

Scenario: Series of commands and output for one rover - full workflow 
When commands are
| 5 5        |
| 1 2 N      |
| LMLMLMLMM  |
Then output is 
| 1 3 N |

Scenario: Commands 
* Commands are 
| Command    | Meaning         | Notes                   |
| 5 5        | Size X Y        | Gives size of map       |
| 1 2 N      | Starting point  | Includes orientation    |
| LMLMLMLMM  | Move            | One or more characters  |

# Size Command

Scenario: Domain Term Size
Size must be at least 1 and less or equal to 100 
* Size restrictions
| Value  | Valid  | Notes          |
| 1      | Yes    |                |
| 100    | Yes    |                |
| 0      | No     | below minimum  |
| 101    | No     | above maximum  |

Scenario: Command to Set the Size
* Convert command to sizes
| Command  | X Size  | Y Size  | Error         | Notes          |
| 5 5      | 5       | 5       |               | From workflow  |
# Possible errors 
| 1 5    | 1    | 5    |               |                  |
| 0 5    | 0    | 5    | SIZE_INVALID  | X Below Minimum  |
| 4 0    | 4    | 0    |               |                  |
| 5 0    | 5    | 0    | SIZE_INVALID  | Y Below Minimum  |
| 100 5  | 100  | 5    |               |                  |
| 101 5  | 101  | 5    | SIZE_INVALID  | X Above Maximum  |
| 5 100  | 5    | 100  |               |                  |
| 5 101  | 5    | 101  | SIZE_INVALID  | Y Above Maximum  |


Scenario: Domain Term Directions
* Directions are 
| Symbol  | Direction  |
| N       | North      |
| S       | South      |
| E       | East       |
| W       | West       |

Scenario: Command to Set Starting Point
# How many more? 
Given size command is 
| Command  | X Size  | Y Size  | 
| 5 10     | 5       | 10      |                 
* Convert command to starting point
| Command  | X  | Y  | Orientation  | Error                      |
| 1 2 N    | 1  | 2  | N            | From workflow              |
# Possible errors 
| 4 10 E   | 4  | 10 | E            | START_INVALID_POSITION     |
| 5 4 E    | 5  | 4  | E            | START_INVALID_POSITION     |
| 4 4 Q    | 4  | 4  | Q            | START_INVALID_ORIENTATION  |

Scenario: Domain Term Moves
* Moves are 
| Symbol  | Move           |
| M       | Move forward   |
| R       | Turn Right 90  |
| L       | Turn left 90   |

Scenario: Move Command 
* Convert Command to Moves   
| Command    | Moves                      | Error         | Notes          |
| LMLMLMLMM  | L, M, L, M, L, M, L, M, M  |               | From workflow  |
# Possible Errors
| Q          | Q                          | MOVE_INVALID  |                |
| MQM        | M, Q, M                    | MOVE_INVALID  |                | 

Scenario: Make a single move 
Given size is 
| X Size  | Y Size  |
| 5       | 5       |
And initial position is 
| X  | Y  | Orientation  |
| 1  | 2  | N            |
When move is 
| L | 
Then final position is 
| X  | Y  | Orientation  |
| 1  | 2  | E            |

Scenario: Business Rule Make a move
* Make a move
| X  | Y  | Orientation  | Move  | New X  | New Y  | New Orientation  |
| 2  | 2  | N            | M     | 2      | 3      | N                |
| 2  | 2  | W            | M     | 1      | 2      | W                |
| 2  | 2  | S            | M     | 2      | 1      | S                |
| 2  | 2  | E            | M     | 2      | 3      | E                |
| 2  | 2  | E            | R     | 2      | 2      | S                |
| 2  | 2  | E            | L     | 2      | 2      | N                |

Scenario: Business Rule Make a move on the border
# Some possible errors
Given size command is 
| Command  | X Size  | Y Size  | 
| 5 10     | 5       | 10      |
* Make a move on the border
| X  | Y  | Orientation  | Move  | New X  | New Y  | New Orientation  | Error   |
| 0  | 0  | W            | M     | 0      | 0      | W                | W_EDGE  |
| 0  | 0  | S            | M     | 0      | 0      | S                | S_EDGE  |
| 0  | 9  | W            | M     | 0      | 9      | W                | W_EDGE  |
| 0  | 9  | N            | M     | 0      | 9      | N                | N_EDGE  |
| 4  | 9  | N            | M     | 4      | 9      | N                | N_EDGE  |
| 4  | 9  | E            | M     | 4      | 9      | E                | E_EDGE  |
| 4  | 0  | S            | M     | 0      | 0      | S                | S_EDGE  |
| 4  | 0  | E            | M     | 0      | 0      | E                | E_EDGE  |



# These are sequence of command issues

Scenario: Series of commands and output for one rover - missing command 
When sequence of commands is
| Command    | Error            |
| 1 2 N      | NO_SIZE_COMMAND  |
| LMLMLMLMM  |                  |
Then output is 
| NO_SIZE_COMMAND |

Scenario: Series of commands and output for one rover - missing command due to error
When sequence of commands is
| Command    | Notes            |
| 0 5        | SIZE_INVALID     |
| 1 2 N      | NO_SIZE_COMMAND  |
| LMLMLMLMM  |                  |
Then output is 
| SIZE_INVALID    |
| NO_SIZE_COMMAND |


