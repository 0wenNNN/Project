*-------------------------------------------------------
* STARTING MEMORY ADDRESS FOR THE PROGRAMME $1000
*-------------------------------------------------------
	ORG $1000
*-------------------------------------------------------
*CHOOSE TO BE A MINI KNIGHT OR A TINY EXPLORER
*-------------------------------------------------------

*-------------------------------------- -----------------
*VALIDATION VALUES TO BE USED, MODIFY AS NEEDED
*-------------------------------------------------------
EXIT		EQU 0			;USED TO EXIT ASSEMBLY PROGRAM
MIN_POTIONS	EQU 1			;MIN NUMBER OF SMALL POTIONS
MAX_POTIONS	EQU 9			;MAX NUMBER OF SMALL POTIONS
DAMAGE      EQU 25          ;DMG VALUE
HEAL        EQU 10          ;HOW MUCH THE POTION HEALS


;A2 WILL BE USED FOR ENEMY ARRAY
;A4 WILL BE USED FOR PLAYER POSITION
;A5 WILL BE USED FOR CLASSES
;D2 WILL BE USED FOR HEALTH
;D4 IS THE CURRENT CLASS
;D7 IS JUST HOW MANY TURNS WILL GO
*START OF GAME
START:
    MOVE.B  #10,D7     ; 10 turns BEFORE GAME ENDS
    BSR     WELCOME
    BSR     GAME
    SIMHALT

*GAME LOOP
    ORG     $3000           ;THE REST OF THE PROGRAM IS TO BE LOCATED FROM 3000 ONWARDS

*-------------------------------------------------------
*-------------------GAME SUBROUTINE---------------------
*-------------------------------------------------------
GAME:
    BSR     GAMELOOP        ;BRANCH TO GAMELOOP SUBROUTINE
    RTS                     ;RETURN FROM GAME: SUBROUTINE
          
END:
    SIMHALT

*-------------------------------------------------------
*-------------------WELCOME SUBROUTINE------------------
*-------------------------------------------------------
WELCOME:
    BSR     ENDL
    LEA     WELCOME_MSG,A1
    MOVE.B  #14,D0
    TRAP    #15          ; print message

    MOVE.B  #4,D0        ; read number
    TRAP    #15          ; result goes into D1
    MOVE.L  D1 ,$4000
    MOVE.L  $4000,D4
    


    CMP.B   #1,D4        ;CHECK INPUT, IF 1 GO TO OPTION 1
    BEQ     WARRIOR

    CMP.B   #2,D4       ;CHECK INPUT, IF 2, GO OPTION 2
    BEQ     ROGUE
    
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     CONTINUE        ;BRANCH TO CONTINUE SUBROUTINE 
    RTS                     ;RETURN FROM WELCOME: SUBROUTINE
WARRIOR:
    MOVE.L  HEALTH_1,D2
    RTS
ROGUE:
    MOVE.L HEALTH_2,D2
    RTS
*-------------------------------------------------------
*---------GAMEPLAY INPUT VALUES SUBROUTINE--------------
*-------------------------------------------------------    
INPUT:
    TST.W   POTION_COUNT    ;cmp potion with 0
    BEQ     SKIP_POTIONS     ; none available
    BSR     POTION_ROUTINE  ; use potion
    RTS
POTION_ROUTINE:
    BSR     POTIONS         ;BRANCH TO POTION INPUT SUBROUTINE
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     CONTINUE        ;BRANCH TO CONTINUE SUBROUTINE
    BSR     WEAPONS         ;BRANCH TO WEAPONS INPUT SUBROUTINE
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     CONTINUE        ;BRANCH TO CONTINUE SUBROUTINE
    RTS
;THE SAME AS THE ORIGINAL INPUT BUT IT SKIPS THE POTION PART
SKIP_POTIONS:
    BSR     WEAPONS
    BSR     ENDL
    BSR     CONTINUE
    RTS
*-------------------------------------------------------
*----------------GAMELOOP (MAIN LOOP)-------------------
*------------------------------------------------------- 
GAMELOOP:
    ; Your existing game logic:
    BSR     INPUT
    BSR     UPDATE
    BSR     DRAW
    BSR     GAMEPLAY
    BSR     HUD
    
    ;check health before that, if 0 then game over
    TST.L   D2
    BLE     GAME_OVER
    ; Decrease loop counter and check if its 0 then you win!
    SUB.B  #1,D7
    TST.B  D7
    BEQ    YOU_WIN    ; if 0 turns left ? game ends
    BRA    GAMELOOP     ; else continue      

*-------------------------------------------------------
*----------------UPDATE QUEST PROGRESS------------------
*  COMPLETE QUEST
*------------------------------------------------------- 
UPDATE:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE  
 
    LEA     UPDATE_MSG,A1   ;ASSIGN MESSAGE TO ADDRESS REGISTER A1 
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0

    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    RTS                     

*-------------------------------------------------------
*-----------------DRAW QUEST UPDATES--------------------
* DRAW THE GAME PROGRESS INFORMATION, STATUS REGARDING
* QUEST
*------------------------------------------------------- 
DRAW:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE            
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE    
    LEA     DRAW_MSG,A1     ;ASSIGN MESSAGE TO ADDRESS REGISTER A1     
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    RTS                     ;RETURN FROM DRAW: SUBROUTINE

*-------------------------------------------------------
*--------------------POTIONS INVENTORY---------------------
* NUMBER OF POTIONS TO BE USED IN A QUEST 
*------------------------------------------------------- 
;D6 IS AMOUNT OF POTIONS YOU WANT TO USE
POTIONS:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    
    LEA     POTIONS_RECAP,A1  ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15 
    
    MOVE.W  POTION_COUNT,D1           
    MOVE.B  #3,D0           ;MOVE LITERAL 3 TO DO    
    TRAP    #15 
    
    LEA     POTIONS_MSG,A1  ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    MOVE.B  #4,D0           ;HOW MANY POTIONS YOU WANT TO USE
    TRAP    #15
    MOVE.B  D1,D6
   
    TST.B D6                ;incase they type 0
    BEQ DONE_HEAL
    
    CMP.W   POTION_COUNT,D6
    BGT     CLAMP_TO_MAX
    BRA     GET_HEAL

CLAMP_TO_MAX:
    MOVE.B POTION_COUNT,D6 ;automatically pushes the number in potion count to d6 if you input more than you own
    
GET_HEAL:
    TST.B D6
    BEQ DONE_HEAL

    ADD.L #HEAL,D2
    SUB.W #1,POTION_COUNT
    SUB.B #1,D6
    
    LEA     HEAL_MSG,A1  ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15
    
    MOVE.L  D2,D1           ;MOVE D2 AND MOVE TO D1
    MOVE.B  #3,D0           ;MOVE LITERAL 3 TO D0    
    TRAP    #15
    
    BRA GET_HEAL
DONE_HEAL:
    BSR DECORATE
    RTS
*-------------------------------------------------------
*-------------------------WEAPONS-----------------------
* NUMBER OF WEAPONS
*-S------------------------------------------------------   
WEAPONS:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    
    CMP.B   #1,D4        ;CHECK INPUT, IF 1 GO TO WARRIOR WEAPON
    BEQ     WARRIOR_WEAPON

    CMP.B   #2,D4         ;CHECK INPUT, IF 2 GO TO ROGUE WEAPON
    BEQ     ROGUE_WEAPON
    
WARRIOR_WEAPON:
    LEA     WEAPONS_MSG_1,A1  ;ASSIGN MESSAGE TO ADDRESS REGISTER A1      
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    RTS                     ;RETURN FROM WEAPONS: SUBROUTINE
ROGUE_WEAPON:
    LEA     WEAPONS_MSG_2,A1  ;ASSIGN MESSAGE TO ADDRESS REGISTER A1      
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    RTS                     ;RETURN FROM WEAPONS: SUBROUTINE
*-------------------------------------------------------
*---GAME PLAY (QUEST PROGRESS)--------------------------
*------------------------------------------------------- 
GAMEPLAY:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    
    LEA     GAMEPLAY_MSG,A1 ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    MOVE.B  #4,D0           ;MOVE LITERAL 4 TO DO               
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    BSR     PATH            ;BRANCH TO PATH
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    ;BSR     COLLISION       ;BRANCH TO COLLISION SUBROUTINE
    RTS                     ;RETURN FROM GAMEPLAY: SUBROUTINE

PATH:
    LEA     CHOOSE_MSG,A1 ;SHOWS THE PATH OPTIONS
    MOVE.B  #14,D0        ; PRINT MESSAGE
    TRAP    #15
    
    MOVE.B  #4,D0        ; read number
    TRAP    #15          ; result goes into D0
    CMP.B   #1,D1
    BEQ     RISKY_PATH       ;MOVES TO RISKY PATH LOGIC
    CMP.B   #2,D1
    BEQ     SAFER_PATH       ;MOVES TO SAFE PATH LOGIC
    BRA     PATH
RISKY_PATH:
    BSR     CHECK_FOR_POTION
    MOVE.W  #8,D0       ; Task 8: Get System Time 
    TRAP    #15         ; Time goes into D1
    
    ANDI.W  #$F,D1      ; use and to cut the value down so D1 is now a random number between 0 and 15.

    CMP.W   #13,D1      ; 13/15 close enough to, 70ish% (found this idea online, it works)
    BLT     COLLISION   ; If D1 < 10, trigger the fight!
    BRA     SAFE_MESSAGE ; If D1 is 11, 12, 13, 14, or 15, you're safe.
SAFER_PATH:
    MOVE.W  #8,D0       ; Get System Time
    TRAP    #15         ; Time goes into D1
    
    ANDI.W  #$F,D1      ; Mask to range 0-15

    CMP.W   #6,D1       ; 6/15 which is 40% of an encounter
    BLT     COLLISION   ; ~40% chance to branch to the fight
    BRA     SAFE_MESSAGE ; ~60% chance you are safe
CHECK_FOR_POTION:
    MOVE.W  #8,D0            ; Get System Time
    TRAP    #15               ; Time goes into D1
    ANDI.W  #$F,D1            ; Mask to 0-15 (0-15 range)

    CMP.W   #6,D1
    BGE     NO_POTION         ; If D1 >= 5, skip potion

GAIN_POTION:
    LEA     GAIN_POTION_MSG,A1
    MOVE.B  #14,D0
    TRAP    #15
    
    ADD.W   #1,POTION_COUNT

NO_POTION:
    RTS
SAFE_MESSAGE:
    LEA     SAFE_MSG,A1  
    MOVE    #14,D0              
    TRAP    #15             ;PRINT ENCOUNTER MSG
    RTS
*-------------------------------------------------------
*-----------------HEADS UP DISPLAY (SCORE)--------------
*-------------------------------------------------------   
HUD:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE
    
    LEA     HUD_MSG,A1      ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    MOVE.L  D2,D1           ;MOVE D2 AND MOVE TO D1
    MOVE.B  #3,D0           ;MOVE LITERAL 3 TO D0   
    TRAP    #15               
    
    BSR     DECORATE        ;BRANCH TO DECORATE SUBROUTINE    
    RTS                     

*-------------------------------------------------------
*-----------------------BEING ATTACKED------------------
* THIS COULD BE USED FOR COLLISION DETECTION
*-------------------------------------------------------
;A2 WILL BE USED FOR ENEMY ARRAY
COLLISION:
    LEA     ENCOUNTER_MSG,A1  
    MOVE    #14,D0              
    TRAP    #15             ;PRINT ENCOUNTER MSG   
    BSR     CHECK_CLASS     ;CHECK FOR CLASS
    RTS                     ;RETURN FROM COLLISION: SUBROUTINE  
CHECK_CLASS:
    CMP.B   #1,D4         ;CHECK INPUT, IF 1 GO TO OPTION 1
    BEQ     WARRIOR_DODGE

    CMP.B   #2,D4         ;CHECK INPUT, IF 2, GO OPTION 2
    BEQ     ROGUE_DODGE
    
    RTS
WARRIOR_DODGE:
    MOVE.W  #8,D0       ; Task 8: Get System Time 
    TRAP    #15         ; Time goes into D1
    
    ANDI.W  #$F,D1      ; use and to cut the value down so D1 is now a random number between 0 and 15.
    CMP.W   #4,D1      
    
    BLT COLLISION_MISS 
    BGE COLLISION_HIT
ROGUE_DODGE:
    MOVE.W  #8,D0       ;Get System Time 
    TRAP    #15         ; Time goes into D1
    
    ANDI.W  #$F,D1      ; use and to cut the value down so D1 is now a random number between 0 and 15.
    CMP.W   #9,D1      
    BLT COLLISION_MISS 

    BGE COLLISION_HIT
COLLISION_HIT: 
    LEA     HIT_MSG,A1     ;just printing the hit msg
    MOVE    #14,D0
    TRAP    #15

    MOVE.L  D2,D1        ; load current health
    SUB.L   #DAMAGE,D1     ; subtract damage
    MOVE.L  D1,D2        ; store back
    

    CMP.L   #0,D1
    BGT     SAFE

    CLR.L   D2           ; if <= 0, set to 0

SAFE:
    RTS

COLLISION_MISS:
    LEA     DODGE_MSG,A1		;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE    #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    RTS                     ;RETURN FROM COLLISION_MISS: SUBROUTINE
*-------------------------------------------------------
*--------------------------LOOP-------------------------
*-------------------------------------------------------
LOOP:
    MOVE.B  #5, D3          ;LOOP COUNTER D3=5
NEXT:
    LEA     LOOP_MSG,A1     ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO          
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    SUB     #1,D3           ;DECREMENT LOOP COUNTER
    BNE     NEXT            ;REPEAT UNTIL D0=0

*-------------------------------------------------------
*------------------SCREEN DECORATION--------------------
*-------------------------------------------------------
DECORATE:
    MOVE.B  #6, D3         ;LOOP COUNTER D3=60
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
OUT:
    LEA     LOOP_MSG,A1     ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    SUB     #1,D3           ;DECREMENT LOOP COUNTER
    BNE     OUT             ;REPEAT UNTIL D0=0
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    RTS                     ;RETURN FROM DECORATE: SUBROUTINE
    
CLEAR_SCREEN: 
    MOVE.B  #11,D0          ;CLEAR SCREEN
    MOVE.W  #$FF00,D1       ;SET COLOUR
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    RTS                     ;RETURN FROM CLEAR_SCREEN: SUBROUTINE                

*-------------------------------------------------------
*------------------------GAME OVER-------------------------
*-------------------------------------------------------
YOU_WIN:
    CMP     #0,D2
    BLE     GAME_OVER
    
    LEA     WIN_MSG,A1
    MOVE.B  #14,D0
    TRAP    #15 
GAME_OVER:
    LEA     GAME_OVER_MSG,A1
    MOVE.B  #14,D0
    TRAP    #15          ; print game over message
    SIMHALT              ; stop program
    
*-------------------------------------------------------
*------------------------CONTINUE-------------------------
*-------------------------------------------------------
CONTINUE:
    BSR     ENDL            ;BRANCH TO ENDL SUBROUTINE
    LEA     CONTINUE_MSG,A1 ;ASSIGN MESSAGE TO ADDRESS REGISTER A1
    MOVE.B  #14,D0          ;MOVE LITERAL 14 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    
    MOVE.B  #4,D0           ;MOVE LITERAL 4 TO DO
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    RTS                     ;RETURN FROM SUBROUTINE 

ENDL:
    MOVEM.L D0/A1,-(A7)     ;SAVE D0 AND A1
    MOVE    #14,D0          ;MOVE LITERAL 14 TO DO
    LEA     CRLF,A1         ;ASSIGN CRLF TO ADDRESS REGISTER A1
    TRAP    #15             ;TRAP AND INTERPRET VALUE IN D0
    MOVEM.L (A7)+,D0/A1     ;RESTORE D0 AND A1
    RTS                     ;RETURN FROM ENDL: SUBROUTINE
    
*-------------------------------------------------------
*-------------------DATA DECLARATIONS--------------------
*-------------------------------------------------------

CRLF:           DC.B    $0D,$0A,0
WELCOME_MSG:    DC.B    '************************************************************'
                DC.B    $0D,$0A
                DC.B    'WELCOME TO YOUR ADVENTURE! '
                DC.B    $0D,$0A
                DC.B    'CHOOSE YOUR ADVENTURER:'
                DC.B    $0D,$0A
                DC.B    '1. WARRIOR (TANKY)'
                DC.B    $0D,$0A
                DC.B    '2. ROGUE (FAST AND AGILE)'
                DC.B    $0D,$0A
                DC.B    '************************************************************'
                DC.B    $0D,$0A,0
POTIONS_MSG:    DC.B    $0D,$0A
                DC.B    'HOW MANY POTIONS WOULD YOU LIKE TO DRINK: ',0
PLAYER_X        DC.L    1
POTION_COUNT    DC.W    0
HEAL_MSG        DC.B    'YOU GOT HEALED! YOUR HEALTH IS NOW: ',0
GAIN_POTION_MSG DC.B    'YOU GOT A POTION!',0
                DC.B    $0D,$0A,0
WEAPONS_MSG_1:  DC.B    'EQUIP WEAPONS: SWORD,SHIELD.',0
POTIONS_RECAP   DC.B    'YOUR POTIONS',0
                DC.B    $0D,$0A
                DC.B    $0D,$0A,0
WEAPONS_MSG_2:  DC.B    'EQUIP WEAPONS: DAGGER,DAGGER.',0
TURN1_CHECK     DC.B    1
GAMEPLAY_MSG:   DC.B    'ADVENTURE BEGINS!',0
UPDATE_MSG:     DC.B    'UPDATING QUEST STATUS...',0
ENCOUNTER_MSG:  DC.B    'UH OH, FOUND AN ENEMY',0
                DC.B    $0D,$0A,0
SAFE_MSG:       DC.B    'SAFE PATH!',0
DRAW_MSG:       DC.B    'REDRAWING WORLD...',0
HIT_MSG:        DC.B    $0D,$0A
                DC.B    'OH NO! YOU WERE HIT!',0
DODGE_MSG:      DC.B    $0D,$0A
                DC.B    'SAFE! YOU DODGED!.',0
LOOP_MSG:       DC.B    '.',0
WIN_MSG:        DC.B    'YOU MADE IT! YOU WIN!',0
GAME_OVER_MSG:  DC.B    'GAME_OVER',0
CONTINUE_MSG:   DC.B    'PRESS ANYKEY TO CONTINUE: ',0
CHOOSE_MSG:     DC.B    'PICK A PATH (TYPE 1 OR 2)'
                DC.B    $0D,$0A
                DC.B    '************************************************************'
                DC.B    $0D,$0A
                DC.B    'RISKY  :1'
                DC.B    $0D,$0A
                DC.B    'SAFFER :2'
                DC.B    $0D,$0A
                DC.B    '************************************************************'
                DC.B    $0D,$0A,0
HUD_MSG:        DC.B    'HEALTH POINTS: ',0
HEALTH_1:       DC.L    100   ;PLAYERS HEALTH FOR WARRIOR
HEALTH_2:       DC.L    55    ;PLAYERS HEALTH FOR ROGUE

    END START

*~Font name~Courier New~
*~Font size~10~
*~Tab type~1~
*~Tab size~4~
















*~Font name~Courier New~
*~Font size~10~
*~Tab type~1~
*~Tab size~4~
