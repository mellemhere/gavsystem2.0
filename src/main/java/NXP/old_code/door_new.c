#include <LiquidCrystal.h>

#include <MFRC522.h>
#include <LiquidCrystal.h>
#include <SPI.h>
#include <SoftwareSerial.h>

#define SerialrxPin 0
#define SerialtxPin 1
#define doorNumber 208
#define outputDoorSignal 7
#define hasLCDmodule 1
#define hasRFIDmodule 1
#define hasKeyBoardmodule 1
#define KeyBoard_PIN A5
#define SS_PIN 10
#define RST_PIN 9
#define LCD_PIN 8
#define doorTimeOpen 2000

#define turnLightOn 1
#define turnLightOff 0

MFRC522 mfrc522(SS_PIN, RST_PIN);

LiquidCrystal lcd(LCD_PIN);


long lastLCD = 0;
long lastRead = 0;


//Segura o ultimo comando lido
String readCommand = "";

//Se hasLCD = true, existe uma mensagem no LCD e ira ser segurada por 1000 segundos
int hasLCD = 0;
int hasENG = 0;

int allLightsON = 0;

/*
   LIGHTS
 */
int lightsIDS[6] = {
    3, //Ultima luz da sala (19) ID: 0 SL5
    A0, //Penultima luz da sala (18) ID: 1 SL4
    A3, //Segunda luz da sala (17) ID: 2 SL3
    A2, //Primeira luz da sala ID: 3 SL2
    A1, //Luz do quadro ID: 4 SL1
    4 //Ventilador ID: 5 SL8
};

int lightsStates[6] = {//0 = Ligado | 1 = Desligado  //  quarta lampada
    turnLightOff, //Ultima luz da sala (19) // quadro
    turnLightOff, //Penultima luz da sala (18) // primeira
    turnLightOff, //Segunda luz da sala (17) // 
    turnLightOn, //Primeira luz da sala
    turnLightOff, //Luz do quadro
    turnLightOff //Ventilador // b para 5
};


int contadorGenerico;

void setup() {

    pinMode(outputDoorSignal, OUTPUT);
    pinMode(KeyBoard_PIN, INPUT);

    for (contadorGenerico = 0; contadorGenerico < sizeof (lightsIDS) / sizeof (int); contadorGenerico++) {
        pinMode(lightsIDS[contadorGenerico], OUTPUT);
        toogleLight(lightsIDS[contadorGenerico], lightsStates[contadorGenerico]);
    }

    Serial.begin(115200); //Inicia a Serial

    if (hasRFIDmodule) {
        SPI.begin(); //Inicia  SPI bus
        mfrc522.PCD_Init(); //Inicia MFRC52
    }

    if (hasLCDmodule) {
        lcd.begin(20, 2);
        lcd.clear();
    }
}

void wLCD(char* message, int line) {
    lcd.setCursor(0, line);

    lastLCD = millis();
    lcd.print(message);

    hasLCD = 1;
    hasENG = 0;
}

void send(const char message[]) {
    Serial.print(message);
}

void abrePorta() {

    if (hasLCDmodule) {
        lcd.clear();
        lcd.BeepLiga();
        wLCD("Acesso liberado!", 0);
    }

    Serial.print(F("o;o\n"));

    digitalWrite(outputDoorSignal, HIGH);
    delay(doorTimeOpen);
    digitalWrite(outputDoorSignal, LOW);
    Serial.print(F("c;c\n"));

    if (hasLCDmodule) {
        lcd.BeepDesliga();
        wLCD("", 2);
    }
}


//Liga ou desliga luz

void ligaLuz(int index, int state) {
    if (state == HIGH) {
        digitalWrite(index, LOW);
    } else {
        digitalWrite(index, HIGH);
    }
}

//Liga ou desliga luz

void toogleLight(int lightID) {
    for (contadorGenerico = 0; contadorGenerico < sizeof (lightsIDS) / sizeof (int); contadorGenerico++) {
        if (lightID == lightsIDS[contadorGenerico]) {
            int currentState = lightsStates[contadorGenerico];
            lightsStates[contadorGenerico] = !currentState;
            Serial.print("l;");
            Serial.print(lightID);
            Serial.print("-");
            if (currentState == 1) {
                //Se a luz esta ligada vamos desligar
                digitalWrite(lightID, turnLightOff);
                Serial.print("off");
            } else {
                //Vamos ligar
                digitalWrite(lightID, turnLightOn);
                Serial.print("on");
            }
            Serial.print("\n");
        }
    }
}

//Liga ou desliga luz

void toogleLight(int lightID, int state) {
    for (contadorGenerico = 0; contadorGenerico < sizeof (lightsIDS) / sizeof (int); contadorGenerico++) {
        if (lightID == lightsIDS[contadorGenerico]) {
            int currentState = lightsStates[contadorGenerico];
            lightsStates[contadorGenerico] = state;
                Serial.print("l;");
                Serial.print(lightID);
                Serial.print("-");
                if (state == 1) {
                    //liga
                    //Vamos ligar
                    digitalWrite(lightID, turnLightOn);
                    Serial.print("on");
                } else {
                    //desliga
                    digitalWrite(lightID, turnLightOff);
                    Serial.print("off");
                }
            Serial.print("\n");
        }
    }
}

//Compara string ao que foi lido

int Comp(String match) {
    if (readCommand.length() > 0) {
        readCommand.trim();
        if (readCommand == match) {
            readCommand = "";
            return 1;
        } else {
            return 0;
        }
    } else {
        return 0;
    }
}

void loop() {

    if (Serial.available()) {
        readCommand = "";
    }

    while (Serial.available()) {
        delay(3);
        char c = Serial.read();
        readCommand += c;
    }

    checkCommands();

    if (hasKeyBoardmodule) {
        checkCommandsKeyBoard();
    }

    if (hasRFIDmodule) {
        if ((millis() - lastRead) > 3000) {
            checkRFID();
        }
    }

    /*

      FUNCAO PARA LIMPAR O LCD EM 10000 SEGUNDOS

     */
    if (hasLCDmodule) {
        if ((millis() - lastLCD > 10000) && hasLCD) {
            lcd.clear();
            hasLCD = 0;
            hasENG = 0;
        } else {
            if (!hasLCD) {
                if (!hasENG) {
                    lcd.setCursor(0, 0);
                    lcd.print("ENGENHARIA UP");
                    lcd.setCursor(0, 1);
                    lcd.print("LABORATORIO 208");
                    hasENG = 1;
                    delay(500);
                }
            }
        }
    }

}

/*
  LE O RFID
 */
void checkRFID() {
    if (!mfrc522.PICC_IsNewCardPresent()) {
        return;
    }
    if (!mfrc522.PICC_ReadCardSerial()) {
        return;
    }

    Serial.print("e;");
    for (byte i = 0; i < mfrc522.uid.size; i++) {
        Serial.print(mfrc522.uid.uidByte[i]);
    }
    Serial.print("\n");
    lastRead = millis();
}

/*
  CHECK FOR COMMANDS
 */
void checkCommands() {

    if (Comp("o")) {
        abrePorta();
    } else if (Comp("ping")) {
        Serial.println("pong");
    } else if (readCommand.length() > 0) {
        if (readCommand.charAt(0) == 'l') {
            //Comando de luz
            int index = readCommand.charAt(1) - '0';
            int state = readCommand.charAt(2) - '0';
            if (state == 0) {
                toogleLight(lightsIDS[index], turnLightOff);
            } else {
                toogleLight(lightsIDS[index], turnLightOn);
            }
            readCommand = "";
        }
    }



}


/*
 * 
 *  KEYBOARD
 * 
 * 
 */
int buttonPressed = 0;
int recebeuComando = 0;

void checkCommandsKeyBoard() {
    const int LEITURAS = 50;
    long soma = 0;
    long media = 0;
    int maior = 0;
    int menor = 1023;

    for (int i = 0; i < LEITURAS; i++) {
        int leitura = analogRead(KeyBoard_PIN);
        if (leitura <= 368) {
            soma = 0;
            break;
        }

        menor = (menor > leitura) ? leitura : menor;
        maior = (maior < leitura) ? leitura : maior;
        soma += leitura;

        if ((maior - menor) >= 4) {
            soma = 0;
            break;
        }
    }

    media = soma / LEITURAS;

    if (recebeuComando == 1) {
        if (media > 100) {
            return;
        } else {
            recebeuComando = 0;
        }
    }


    if (media <= 1023 && media >= 971) {
        //A - Abre porta
        recebeuComando = 1;
        abrePorta();
    }

    if (media <= 738 && media >= 673) {
        //B - LIGA VENTILADOR
        recebeuComando = 1;
        toogleLight(lightsIDS[5]);
    }

    if (media <= 551 && media >= 513) {
        //C
    }

    if (media <= 437 && media >= 413) {
        //D
    }


    if (media <= 813 && media >= 739) {
        //1 - Liga luz do quadro
        recebeuComando = 1;
        toogleLight(lightsIDS[4]);
    }

    if (media <= 885 && media >= 814) {
        //2 - LIGA PRIMEIRA LUZ DA SALA
        recebeuComando = 1;
        toogleLight(lightsIDS[3]);
    }

    if (media <= 970 && media >= 886) {
        //3 - LIGA SEGUNDA LUZ DA SALA
        recebeuComando = 1;
        toogleLight(lightsIDS[2]);
    }

    if (media <= 594 && media >= 552) {
        //4 - LIGA PENULTIMA LUZ DA SALA
        recebeuComando = 1;
        toogleLight(lightsIDS[1]);
    }

    if (media <= 630 && media >= 595) {
        //5 - LIGA ULTIMA LUZ DA SALA
        recebeuComando = 1;
        toogleLight(lightsIDS[0]);
    }

    if (media <= 672 && media >= 631) {
        //6
    }

    if (media <= 465 && media >= 438) {
        //7
    }

    if (media <= 488 && media >= 466) {
        //8
    }

    if (media <= 512 && media >= 489) {
        //9
    }

    if (media <= 412 && media >= 398) {
        //#
    }
    if (media <= 397 && media >= 383) {
        //0
    }

    if (media <= 382 && media >= 369) {
        recebeuComando = 1;
        //*
        int c = 0;
        if (allLightsON) {
            //Desliga todas as luzes
            allLightsON = turnLightOff;
        } else {
            //Liga todas as luzes
            allLightsON = turnLightOn;
        }
        for (c = 0; c < (sizeof (lightsIDS) / sizeof (int) - 1); c++) {
            toogleLight(lightsIDS[c], allLightsON);
        }
    }
}

void reset() {
    setup();
}