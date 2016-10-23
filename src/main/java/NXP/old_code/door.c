

#include <LiquidCrystal.h>
#include <SPI.h>
#include <MFRC522.h>


MFRC522 mfrc522(10, 9);
MFRC522::MIFARE_Key key;

int porta = 208;
int saidaPulso = 8;

int luzLigado = 0;



char inDataName[20];
char inCharName = -1;
byte indexName = 0;

/*
 TIME VARIABLES
 */
long lastLCD = 0;
long lastRead = 0;
int hasLCD = 0;
int hasENG = 0;

int luzTrazeira = A4; //19
int luzPenultima = A3; //18
int luzMeio = A5; //17
int luzPrimeira = A2; //16
int luzQuadro = A1; //15 Quadro
int projetor = A0; //14 Ventilador


String readString = "";

LiquidCrystal lcd(7, 6, 5, 4, 3, 2);

void setup() {

    pinMode(projetor, OUTPUT);
    pinMode(saidaPulso, OUTPUT);
    pinMode(luzTrazeira, OUTPUT);
    pinMode(luzPenultima, OUTPUT);
    pinMode(luzMeio, OUTPUT);
    pinMode(luzPrimeira, OUTPUT);
    pinMode(luzQuadro, OUTPUT);


    digitalWrite(projetor, 1);
    digitalWrite(luzTrazeira, 1);
    digitalWrite(luzPenultima, 1);
    digitalWrite(luzMeio, 1);
    digitalWrite(luzPrimeira, 1);
    digitalWrite(luzQuadro, 1);



    Serial.begin(115200); //Inicia a serial
    SPI.begin(); //Inicia  SPI bus
    mfrc522.PCD_Init(); //Inicia MFRC52

    //Prepara chave - padrao de fabrica = FFFFFFFFFFFFh
    for (byte i = 0; i < 6; i++) key.keyByte[i] = 0xFF;
    lcd.begin(20, 2);
    lcd.clear();
}

/*
          
 */
void wLCD(char* message, int line) {
    lcd.setCursor(0, line);

    lastLCD = millis();
    lcd.print(message);

    hasLCD = 1;
    hasENG = 0;
}

void send(char* message) {
    Serial.print(message);
}

void pulso() {
    lcd.clear();
    wLCD("Acesso liberado!", 0);
    //wLCD(inDataName, 1);
    Serial.print(F("o;o\n"));
    digitalWrite(saidaPulso, HIGH);
    delay(1000);
    digitalWrite(saidaPulso, LOW);
    Serial.print(F("c;c\n"));
}

/*
       
 FUNCOES DE ABRIR E FECHAR A PORTA
       
 */
void fechaPorta() {
    wLCD("Acesso negado!", 0);
    digitalWrite(saidaPulso, LOW);
    digitalWrite(A0, LOW);
    Serial.print(F("c;c\n"));
}

void abrePorta() {
    pulso();
}

int toogleState(int currentState) {
    if (currentState == 1) {
        return 0;
    } else {
        return 1;
    }
}

void ligaLuz(int index, int state) {
    if (state == HIGH) {
        digitalWrite(index, LOW);
    } else {
        digitalWrite(index, HIGH);
    }
}

int Comp(String match) {
    if (readString.length() > 0) {
        readString.trim();
        if (readString == match) {
            readString = "";
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
        readString = "";
    }

    while (Serial.available()) {
        delay(3);
        char c = Serial.read();
        readString += c;
    }

    if (Comp("o")) {
        abrePorta();
    }

    /*
     Luzes
     */
    if (Comp("l140")) {
        ligaLuz(projetor, 0);
    }
    
    if (Comp("l141")) {
        ligaLuz(projetor, 1);
    }
    
    //15
    if (Comp("l150")) {
        ligaLuz(luzQuadro, 0);
    }
    
    if (Comp("l151")) {
        ligaLuz(luzQuadro, 1);
    }    

    //16
    if (Comp("l161")) {
        ligaLuz(luzPrimeira, 1);
    }

    if (Comp("l160")) {
        ligaLuz(luzPrimeira, 0);
    }
    
    //17
    if (Comp("l170")) {
        ligaLuz(luzMeio, 0);
    }
    
    if (Comp("l171")) {
        ligaLuz(luzMeio, 1);
    }
    
    //18
    if (Comp("l180")) {
        ligaLuz(luzPenultima, 0);
    }
    
    if (Comp("l181")) {
        ligaLuz(luzPenultima, 1);
    }

    //19
    if (Comp("l190")) {
        ligaLuz(luzTrazeira, 0);
    }

    if (Comp("l191")) {
        ligaLuz(luzTrazeira, 1);
    }

    
    if ((millis() - lastRead) > 3000) {
        modo_leitura();
    }
    /*
              
     FUNCAO PARA LIMPAR O LCD EM 10000 SEGUNDOS
           
     */
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

void modo_leitura() {
    //Aguarda cartao
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


