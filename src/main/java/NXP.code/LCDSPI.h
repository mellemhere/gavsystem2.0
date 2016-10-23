#ifndef LiquidCrystal_h
#define LiquidCrystal_h

#include <inttypes.h>
#include "mbed.h"


// commands
#define LCD_CLEARDISPLAY 0x01
#define LCD_RETURNHOME 0x02
#define LCD_ENTRYMODESET 0x04
#define LCD_DISPLAYCONTROL 0x08
#define LCD_CURSORSHIFT 0x10
#define LCD_FUNCTIONSET 0x20
#define LCD_SETCGRAMADDR 0x40
#define LCD_SETDDRAMADDR 0x80

// flags for display entry mode
#define LCD_ENTRYRIGHT 0x00
#define LCD_ENTRYLEFT 0x02
#define LCD_ENTRYSHIFTINCREMENT 0x01
#define LCD_ENTRYSHIFTDECREMENT 0x00

// flags for display on/off control
#define LCD_DISPLAYON 0x04
#define LCD_DISPLAYOFF 0x00
#define LCD_CURSORON 0x02
#define LCD_CURSOROFF 0x00
#define LCD_BLINKON 0x01
#define LCD_BLINKOFF 0x00

// flags for display/cursor shift
#define LCD_DISPLAYMOVE 0x08
#define LCD_CURSORMOVE 0x00
#define LCD_MOVERIGHT 0x04
#define LCD_MOVELEFT 0x00

// flags for function set
#define LCD_8BITMODE 0x10
#define LCD_4BITMODE 0x00
#define LCD_2LINE 0x08
#define LCD_1LINE 0x00
#define LCD_5x10DOTS 0x04
#define LCD_5x8DOTS 0x00
#define BEEP_on 0x80
#define BEEP_off 0x7F

class LCDSPI{
public:

    LCDSPI(PinName mosi,PinName miso,PinName sclk, PinName cs);  //SPI to ShiftRegister 74HC595 ##########

    void init(uint8_t fourbitmode, uint8_t rs, uint8_t rw, uint8_t enable,
              uint8_t d0, uint8_t d1, uint8_t d2, uint8_t d3,
              uint8_t d4, uint8_t d5, uint8_t d6, uint8_t d7);

    void initSPI(uint8_t _ssPin); //SPI ##################################

    void begin(uint8_t cols, uint8_t rows, uint8_t charsize = LCD_5x8DOTS);

    void clear();
    void home();
    void bitWrite(uint8_t*x, uint8_t n, uint8_t value);
    void noDisplay();
    void display();
    void noBlink();
    void blink();
    void noCursor();
    void cursor();
    void scrollDisplayLeft();
    void scrollDisplayRight();
    void leftToRight();
    void rightToLeft();
    void autoscroll();
    void noAutoscroll();
    
    int printf(const char* format, ...); 
     
    void createChar(uint8_t, uint8_t[]);
    void setCursor(uint8_t, uint8_t);
    virtual size_t write(uint8_t);
    void command(uint8_t);
    void BeepDesliga();
    void BeepLiga();
    int _putc(int value);
    
private:

    SPI              m_SPI;
    DigitalOut       m_CS;

    uint8_t send(uint8_t, uint8_t);
    void spiSendOut();      // SPI ###########################################
    void write4bits(uint8_t);
    void write8bits(uint8_t);
    void pulseEnable();


    uint8_t _rs_pin; // LOW: command.  HIGH: character.
    uint8_t _rw_pin; // LOW: write to LCD.  HIGH: read from LCD.
    uint8_t _enable_pin; // activated by a HIGH pulse.
    uint8_t _data_pins[8];

    //SPI #####################################################################
    uint8_t _bitString; //for SPI  bit0=not used, bit1=RS, bit2=RW, bit3=Enable, bits4-7 = DB4-7
    bool _usingSpi;  //to let send and write functions know we are using SPI
    int _StateBeep;
    uint8_t _latchPin;
    uint8_t _clockDivider;
    uint8_t _dataMode;
    uint8_t _bitOrder;//SPI ####################################################

    uint8_t _displayfunction;
    uint8_t _displaycontrol;
    uint8_t _displaymode;

    uint8_t _initialized;

    uint8_t _numlines,_currline;
};

#endif