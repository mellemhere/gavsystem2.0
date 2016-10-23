#include "mbed.h"
#include "MFRC522.h"
#include "LCDSPI.h"
#include "ST7567.h"

#define luzLigada 1
#define luzDesligada 0
#define intervaloDeTempoEntreCorrentes 2000 //Em MS
#define tempoDeAberturaDaPorta 2 //Em segundos
#define BUFFER_MAXIMO 10


/*

#define    SPI_MOSI P1_4 //cor de viado
#define    SPI_MISO P1_3 //branco
#define    SPI_SCK PF_4 //laranja
#define    SPI_CS P1_5 //RFID verde escuro
#define    MF_RESET P2_6
#define    LCD_CS P2_12


//Azul escuro VCC
//Azul claro GND


*/




#define    SPI_MOSI P1_4 //cor de pele
#define    SPI_MISO P1_3 //branco
#define    SPI_SCK PF_4 //laranja
#define    SPI_CS P1_7// RFID verde escuro 
#define    MF_RESET P2_6 //Marrom
#define    LCD_CS P3_5 //verde claro




Serial BT(USBTX,USBRX);
//Serial BT(D1, D0);

/*

    FUNCOES
*/

void inicia();
//LUZ
void mudarLuz(DigitalInOut pino, int IndexDoEstado);
void mudarLuz(DigitalInOut pino, int estado, int IndexDoEstado);
void mudarTodas();
//LEITURA
void lerSensores();
void lerTeclado();
//PORTA
void abrirPorta();
//Outras
bool Comp (char *string2); //Compara as strings
void lerBuffer();
void limpaBuffer();
void processaComandos();
void lerRFID();


//<--- FIM FUNCOES

//Luzes
DigitalInOut luz1(P6_1);
DigitalInOut luz2(P6_0);

DigitalInOut luz3(P2_9);

DigitalInOut luz4(P3_2);


DigitalInOut ventilador(P2_12);

//PORTA
DigitalInOut porta(P3_4);

/*

    SENSORES
*/

//Sensor de corrente
AnalogIn sensorDeCorrente(A0);
//Sensor de fumaca
AnalogIn sensorDeFumaca(A2);
//Teclado
AnalogIn teclado(A1);
/*

    VARIAVEIS
*/


//Timers 'couse delays are spooky
long ultimaLeituraDeCorrente = 0;
long ultimaLeituraDoRFID = 0;
long ultimaMensagemDoLCD = 0;

int lcdComMensagem = 0;

int estadoDasLuzes = 0;

int estadoDaPorta = 0;

int temComando = 0;

char buffer[BUFFER_MAXIMO];

LCDSPI    LCD(SPI_MOSI, SPI_MISO, SPI_SCK, LCD_CS);
MFRC522    RfChip(SPI_MOSI, SPI_MISO, SPI_SCK, SPI_CS, MF_RESET);

ST7567 lcdx(D11, D13, D12, D9, D10); // mosi, sclk, reset, A0, nCS


/*

    OUTROS

*/
Timer tempo;

int estados[5] = {
    luzDesligada,
    luzDesligada,
    luzDesligada,
    luzDesligada,
    luzDesligada,
};



void inicia()
{

    /*

        INICIA ESTADO DAS LUZES
    */

    luz1.output();
    luz1 = estados[0];

    luz2.output();
    luz2 = estados[1];

    luz3.output();
    luz3 = estados[2];

    luz4.output();
    luz4 = estados[3];


    porta.output();

    ventilador.output();
    ventilador = estados[4];


    /*
        RFID E LCD
    */
    RfChip.PCD_Init();
    LCD.begin(20,2);

}

/*

    RFID

*/

void lerRFID()
{

    //Nao vamos ler toda hora
    if((tempo.read_ms() - ultimaLeituraDoRFID) < 1000) {
        //BT.printf("tempo");
        //return;
    }
    ultimaLeituraDoRFID = tempo.read_ms();
    if ( ! RFID.PICC_IsNewCardPresent()) {
        BT.printf("ncartao\n");
        //NENHUM CARTAO PRESENTE
        return;
    }

    // Select one of the cards
    if ( ! RFID.PICC_ReadCardSerial()) {
        BT.printf("erro de leitura\n");
        //NADA PARA LER OU ERRO DE LEITURA?
        return;
    }

    BT.printf("e;");
    int i ;
    char tempBuffer[30];
    for (i = 0; i < RFID.uid.size; i++) {
        tempBuffer[i] = RFID.uid.uidByte[i];
    }
    tempBuffer[i]= '\0';

    BT.printf(tempBuffer);
    BT.printf("\n");
    }

    void escreveNoLCD(char* mensagem)
    {
     LCD.clear();
     ultimaMensagemDoLCD = tempo.read_ms() - ultimaMensagemDoLCD;

     lcdComMensagem = 1;
     LCD.printf(mensagem);
     }



    /*

        LUZ E PORTA

    */

    void abrirPorta() {

        porta = 1;
        BT.printf("o;o\n"); //Avisa ao servidor que a porta foi aberta
        wait(tempoDeAberturaDaPorta);
        BT.printf("c;c\n"); //Avisa ao servidor que a porta foi aberta
        porta = 0;

    }


    void mudarLuz(DigitalInOut pino, int IndexDoEstado) {
        pino = ~estados[IndexDoEstado];
        estados[IndexDoEstado] = ~estados[IndexDoEstado];
    }

    void mudarLuz(DigitalInOut pino, int estado, int IndexDoEstado) {
        pino = estado;
        estados[IndexDoEstado] = estado;
    }

    void mudarTodas() {
        if(estadoDasLuzes) {

            /*
                Vamos desligar tudo
            */

            luz1 = luzLigada;
            estados[0] = luzLigada;
            luz2 = luzLigada;
            estados[1] = luzLigada;
            luz3 = luzLigada;
            estados[2] = luzLigada;
            luz4 = luzLigada;
            estados[3] = luzLigada;

            //ventilador = ~estadoDasLuzes; //Ignora o ventilador para ligar
            estadoDasLuzes = luzLigada;


        } else {


            /*
                Vamos ligar tudo
            */

            luz1 = luzDesligada;
            estados[0] = luzDesligada;
            luz2 = luzDesligada;
            estados[1] = luzDesligada;
            luz3 = luzDesligada;
            estados[2] = luzDesligada;
            luz4 = luzDesligada;
            estados[3] = luzDesligada;


            ventilador = luzDesligada;
            estados[4] = luzDesligada;

            estadoDasLuzes = luzDesligada;
        }
    }


    /*


        LEITURA DE COMANDOS

    */


    bool Comp (char *string2) {
        if (!strcmp(buffer,string2))//if (buffer[count] != string2[count])
            return false;
        return true;
    }


    void lerBuffer() {
        int count = 0;
        if(!BT.readable()) {
            return;
        }
        temComando = 1;
        while (count < (BUFFER_MAXIMO - 1)) {
            buffer[count] = BT.getc();
            if ((buffer[count] == 0x0a) || (buffer[count] == 0x0d))
                break;
            count ++;
        }

        buffer[count] = 0;
    }




    void processaComandos() {
        temComando = 0;


        if (Comp("o;o")) {
            abrirPorta();
        }

        /*
          Luzes
         */
//Luz 1
        if (Comp("l10")) {
            mudarLuz(luz1, 0, 0);
        }

        if (Comp("l11")) {
            mudarLuz(luz1, 1, 0);
        }

//Luz 2
        if (Comp("l20")) {
            mudarLuz(luz2, 1, 1);
        }

        if (Comp("l21")) {
            mudarLuz(luz2, 0, 1);
        }

//Luz 3
        if (Comp("l31")) {
            mudarLuz(luz3, 1, 2);
        }

        if (Comp("l30")) {
            mudarLuz(luz3, 0, 2);
        }

//Luz 4
        if (Comp("l40")) {
            mudarLuz(luz4, 1, 3);
        }

        if (Comp("l41")) {
            mudarLuz(luz4, 0, 3);
        }

//Luz 5/Ventilador
        if (Comp("l50")) {
            mudarLuz(ventilador, 1, 4);
        }

        if (Comp("l51")) {
            mudarLuz(ventilador, 0, 4);
        }

        if (Comp("ping")) {
            BT.printf("pong");
        }


        limpaBuffer();
    }


    void limpaBuffer() {
        buffer[0] = 0;
    }


    void lerSensores() {

//SENSOR DE FUMACA
        if(sensorDeFumaca < 0.500) {
            //FUMACA DETECTADA
            BT.printf("f;f\n"); //Manda para o servidor que foi detectado fumaca
        }



        if((tempo.read_ms() - ultimaLeituraDeCorrente) > 3000) {
            ultimaLeituraDeCorrente = tempo.read_ms();

            //SENSOR DE CORRENTE
            int correnteAtual = ((float)teclado*1023);
            BT.printf("c;%d\n",correnteAtual); //Manda para o servidor que foi detectado fumaca
        }
    }



    void lerTeclado() {
        const int LEITURAS = 300;
        long soma = 0;
        long media = 0;
        int maior = 0;
        int menor = 1023;

        for (int i = 0; i < LEITURAS; i++) {
            int leitura = ((float)teclado*1023);
            if (leitura <= 200) {
                soma=0;
                break;
            }

            menor = (menor > leitura) ? leitura : menor;
            maior = (maior < leitura) ? leitura : maior;
            soma += leitura;
        }

        media = soma / LEITURAS;

        char c = ' ';

        if (media >= 389 && media <= 403 ) {
            c = '1';
        }
        if (media >= 513 && media <= 519)  {
            c = '2';
        }
        if (media >= 673 && media <= 677)  {
            c = '3';
        }
        if (media >= 915 && media <= 950)  {
            c = 'A';
        }
        if (media >= 389 && media <= 385)  {
            c = '4';
        }
        if (media >= 489 && media <= 493)  {
            c = '5';
        }
        if (media >= 632 && media <= 636)  {
            c = '6';
        }
        if (media >= 854 && media <= 859)  {
            c = 'B';
        }
        if (media >= 369 && media <= 375)  {
            c = '7';
        }
        if (media >= 469 && media <= 473)  {
            c = '8';
        }
        if (media >= 593 && media <= 603)  {
            c = '9';
        }
        if (media >= 779 && media <= 781)  {
            c = 'C';
        }
        if (media >= 359 && media <= 362)  {
            c = '*';
        }
        if (media >= 447 && media <= 453)  {
            c = '0';
        }
        if (media >= 562 && media <= 569)  {
            c = '#';
        }
        if (media >= 727 && media <= 730)  {
            c = 'D';
        }

        switch(c) {
            case '1':
                //LIGA LUZ 1
                mudarLuz(luz1, 0);
                break;

            case '2':
                //LIGA LUZ 2
                mudarLuz(luz2, 1);
                break;


            case '3':
                //LIGA LUZ 3
                mudarLuz(luz3, 2);
                break;


            case '4':
                //LIGA LUZ 4
                mudarLuz(luz4, 3);
                break;

            case 'A':
                //ABRE A PORTA
                abrirPorta();
                break;


            case 'B':
                //LIGA VENTILADOR
                mudarLuz(ventilador, 4);
                break;


            case '*':
                //LIGA/DESLIGA TUDO
                mudarTodas();
                break;
        }

    }



    /*

        LOOP PRINCIPAL E INIT

    */



    void loop() {

        lerBuffer();
        lerSensores();
        lerTeclado();
        lerRFID();

        if(temComando) {
            processaComandos();
        }

        /*
            PORTA
        */
        if(porta == 1 && !estadoDaPorta) {
            estadoDaPorta = 1;
            abrirPorta();
        } else {
            estadoDaPorta = 0;
        }


        if(lcdComMensagem == 1) {
            //Tem mensagem, vamos verificar a quanto tempo ela esta la
            //Se for maior que 3 segundos vamos apagar o display
            if((tempo.read_ms() - ultimaMensagemDoLCD) > 3000) {
                lcdComMensagem = 0; //No proximo loop ele ira escrever a mensagem normal
            }
        } else if(lcdComMensagem == 0) {
            //Nao tem nenhuma mensagem, mas temos que escrever a mensagem normal
            escreveNoLCD("Passe seu cartao");
            lcdComMensagem = -1;
        }
    }

    int main() {


        /* lcdx.set_contrast(0x3f);
         lcdx.cls();

         lcdx.locate(0, 1);
         lcdx.printf("LPC General Purpose Shield");
         lcdx.locate(0, 2 + 8);
         lcdx.printf("[OM13082] test program");
         */


        tempo.start();
        ultimaLeituraDeCorrente = tempo.read_ms();

        //pc.baud(115200);//Set baudrate here.
        BT.baud(115200);
        BT.printf("Iniciando...");

        RfChip.PCD_Init();
         inicia();

        //LCD.printf("ACD");

        //Inicia loop principal
         while(1) loop();

    }

