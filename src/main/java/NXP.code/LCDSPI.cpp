#include "mbed.h"
#include "LCDSPI.h"
#include <stdio.h>
#include <string.h>
#include <inttypes.h>

#define LOW 0
#define HIGH 1

// When the display powers up, it is configured as follows:
//
// 1. Display clear
// 2. Function set:
//    DL = 1; 8-bit interface data
//    N = 0; 1-line display
//    F = 0; 5x8 dot character font
// 3. Display on/off control:
//    D = 0; Display off
//    C = 0; Cursor off
//    B = 0; Blinking off
// 4. Entry mode set:
//    I/D = 1; Increment by 1
//    S = 0; No shift
//
// Note, however, that resetting the Arduino doesn't reset the LCD, so we
// can't assume that its in that state when a sketch starts (and the
// LCDSPI constructor is called).
LCDSPI::LCDSPI(PinName mosi,
               PinName miso,
               PinName sclk,
               PinName cs) : m_SPI(mosi, miso, sclk), m_CS(cs)
{

    _usingSpi = true;
    /* Configure SPI bus */
    m_SPI.format(8, 0);
    m_SPI.frequency(8000000);

    /* Release SPI-CS pin */
    m_CS       = 1;
    init(1, 1, 255, 3, 0, 0, 0, 0, 4, 5, 6, 7);
} // End constructor


void LCDSPI::init(uint8_t fourbitmode, uint8_t rs, uint8_t rw, uint8_t enable,
                  uint8_t d0, uint8_t d1, uint8_t d2, uint8_t d3,
                  uint8_t d4, uint8_t d5, uint8_t d6, uint8_t d7)
{
    _rs_pin = rs;
    _rw_pin = rw;
    _enable_pin = enable;

    _data_pins[0] = d0;
    _data_pins[1] = d1;
    _data_pins[2] = d2;
    _data_pins[3] = d3;
    _data_pins[4] = d4;
    _data_pins[5] = d5;
    _data_pins[6] = d6;
    _data_pins[7] = d7;

    //pinMode(_rs_pin, OUTPUT);
    // we can save 1 pin by not using RW. Indicate by passing 255 instead of pin#
    if (_rw_pin != 255) {
        //pinMode(_rw_pin, OUTPUT);
    }
    //pinMode(_enable_pin, OUTPUT);

    if (fourbitmode)
        _displayfunction = LCD_4BITMODE | LCD_1LINE | LCD_5x8DOTS;
    else
        _displayfunction = LCD_8BITMODE | LCD_1LINE | LCD_5x8DOTS;

    begin(20, 2);

    //since in initSPI constructor we set _usingSPI to true and we run it first
    //from SPI constructor, we do nothing here otherwise we set it to false
    if (_usingSpi) { //SPI ######################################################
        ;
    } else {
        _usingSpi = false;
    }
}


int LCDSPI::_putc(int value)
{
    write(value);
    return 1;
}

int LCDSPI::printf(const char* text, ...) {
    while (*text !=0) {
        _putc(*text);
        text++;
    }
    return 0;
}
void LCDSPI::begin(uint8_t cols, uint8_t lines, uint8_t dotsize)
{
    if (lines > 1) {
        _displayfunction |= LCD_2LINE;
    }
    _numlines = lines;
    _currline = 0;

    // for some 1 line displays you can select a 10 pixel high font
    if ((dotsize != 0) && (lines == 1)) {
        _displayfunction |= LCD_5x10DOTS;
    }

    // SEE PAGE 45/46 FOR INITIALIZATION SPECIFICATION!
    // according to datasheet, we need at least 40ms after power rises above 2.7V
    // before sending commands. Arduino can turn on way befer 4.5V so we'll wait 50
    wait_us(50000);
    // Now we pull both RS and R/W low to begin commands
    //digitalWrite(_rs_pin, LOW);
    //digitalWrite(_enable_pin, LOW);
    if (_rw_pin != 255) {
        //  digitalWrite(_rw_pin, LOW);
    }

    //put the LCD into 4 bit or 8 bit mode
    if (! (_displayfunction & LCD_8BITMODE)) {
        // this is according to the hitachi HD44780 datasheet
        // figure 24, pg 46

        // we start in 8bit mode, try to set 4 bit mode
        write4bits(0x03);
        wait_us(4500); // wait min 4.1ms

        // second try
        write4bits(0x03);
        wait_us(4500); // wait min 4.1ms

        // third go!
        write4bits(0x03);
        wait_us(150);

        // finally, set to 4-bit interface
        write4bits(0x02);
    } else {
        // this is according to the hitachi HD44780 datasheet
        // page 45 figure 23

        // Send function set command sequence
        command(LCD_FUNCTIONSET | _displayfunction);
        wait_us(4500);  // wait more than 4.1ms

        // second try
        command(LCD_FUNCTIONSET | _displayfunction);
        wait_us(150);

        // third go
        command(LCD_FUNCTIONSET | _displayfunction);
    }

    // finally, set # lines, font size, etc.
    command(LCD_FUNCTIONSET | _displayfunction);

    // turn the display on with no cursor or blinking default
    _displaycontrol = LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
    display();

    // clear it off
    clear();

    // Initialize to default text direction (for romance languages)
    _displaymode = LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;
    // set the entry mode
    command(LCD_ENTRYMODESET | _displaymode);

}

/**** high level commands, for the user! */
void LCDSPI::clear()
{
    command(LCD_CLEARDISPLAY);  // clear display, set cursor position to zero
    wait_us(2000);  // this command takes a long time!
}

void LCDSPI::home()
{
    command(LCD_RETURNHOME);  // set cursor position to zero
    wait_us(2000);  // this command takes a long time!
}

void LCDSPI::bitWrite(uint8_t * x, uint8_t n, uint8_t value)
{
    if (value)
        *x |= (1 << n);
    else
        *x &= ~(1 << n);
}

void LCDSPI::setCursor(uint8_t col, uint8_t row)
{
    int row_offsets[] = { 0x00, 0x40, 0x14, 0x54 };
    if ( row > _numlines ) {
        row = _numlines-1;    // we count rows starting w/0
    }

    command(LCD_SETDDRAMADDR | (col + row_offsets[row]));
}

// Turn the display on/off (quickly)
void LCDSPI::noDisplay()
{
    _displaycontrol &= ~LCD_DISPLAYON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}
void LCDSPI::display()
{
    _displaycontrol |= LCD_DISPLAYON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}

// Turns the underline cursor on/off
void LCDSPI::noCursor()
{
    _displaycontrol &= ~LCD_CURSORON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}
void LCDSPI::cursor()
{
    _displaycontrol |= LCD_CURSORON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}

// Turn on and off the blinking cursor
void LCDSPI::noBlink()
{
    _displaycontrol &= ~LCD_BLINKON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}
void LCDSPI::blink()
{
    _displaycontrol |= LCD_BLINKON;
    command(LCD_DISPLAYCONTROL | _displaycontrol);
}

// These commands scroll the display without changing the RAM
void LCDSPI::scrollDisplayLeft(void)
{
    command(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVELEFT);
}
void LCDSPI::scrollDisplayRight(void)
{
    command(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVERIGHT);
}

// This is for text that flows Left to Right
void LCDSPI::leftToRight(void)
{
    _displaymode |= LCD_ENTRYLEFT;
    command(LCD_ENTRYMODESET | _displaymode);
}

// This is for text that flows Right to Left
void LCDSPI::rightToLeft(void)
{
    _displaymode &= ~LCD_ENTRYLEFT;
    command(LCD_ENTRYMODESET | _displaymode);
}

// This will 'right justify' text from the cursor
void LCDSPI::autoscroll(void)
{
    _displaymode |= LCD_ENTRYSHIFTINCREMENT;
    command(LCD_ENTRYMODESET | _displaymode);
}

// This will 'left justify' text from the cursor
void LCDSPI::noAutoscroll(void)
{
    _displaymode &= ~LCD_ENTRYSHIFTINCREMENT;
    command(LCD_ENTRYMODESET | _displaymode);
}

// Allows us to fill the first 8 CGRAM locations
// with custom characters
void LCDSPI::createChar(uint8_t location, uint8_t charmap[])
{
    location &= 0x7; // we only have 8 locations 0-7
    command(LCD_SETCGRAMADDR | (location << 3));
    for (int i=0; i<8; i++) {
        write(charmap[i]);
    }
}

/***** mid level commands, for sending data/cmds */

inline void LCDSPI::command(uint8_t value)
{
    send(value, LOW);
}

inline size_t LCDSPI::write(uint8_t value)
{
    send(value, HIGH);
    return 1; // assume sucess
}

/***** low level data pushing commands *****/

void LCDSPI::BeepLiga()
{
    _StateBeep = true;
}

void LCDSPI::BeepDesliga()
{
    _StateBeep = false;
}

// write either command or data, with automatic 4/8-bit selection
uint8_t LCDSPI::send(uint8_t value, uint8_t mode)
{

    if(_StateBeep) {
        _bitString = _bitString | 1;
    } else {
        _bitString = _bitString & 254;
    }


    if (_usingSpi == false) {
        /*digitalWrite(_rs_pin, mode);

        // if there is a RW pin indicated, set it low to Write
        if (_rw_pin != 255) {
            digitalWrite(_rw_pin, LOW);
        }

        if (_displayfunction & LCD_8BITMODE) {
            write8bits(value);
        } else {
            write4bits(value>>4);
            write4bits(value);
        }*/
    } else { //we use SPI  ##########################################
        bitWrite(&_bitString, _rs_pin, mode); //set RS to mode
        spiSendOut();

        //we are not using RW with SPI so we are not even bothering
        //or 8BITMODE so we go straight to write4bits
        write4bits(value>>4);
        write4bits(value);
    }
    return value;
}

void LCDSPI::pulseEnable(void)
{

    bitWrite(&_bitString, _enable_pin, LOW);
    spiSendOut();
    wait_us(1);
    bitWrite(&_bitString, _enable_pin, HIGH);
    spiSendOut();
    wait_us(1);    // enable pulse must be >450ns
    bitWrite(&_bitString, _enable_pin, LOW);
    spiSendOut();
    wait_us(40);   // commands need > 37us to settle
}

void LCDSPI::write4bits(uint8_t value)
{
    if (_usingSpi == false) {
        for (int i = 0; i < 4; i++) {
            //pinMode(_data_pins[i], OUTPUT);
            //digitalWrite(_data_pins[i], (value >> i) & 0x01);
        }
    } else { //we use SPI ##############################################
        for (int i = 4; i < 8; i++) {
            //we put the four bits in the _bit_string
            bitWrite(&_bitString, i, ((value >> (i - 4)) & 0x01));
        }
        //and send it out
        spiSendOut();
    }
    pulseEnable();
}

void LCDSPI::write8bits(uint8_t value)
{
    for (int i = 0; i < 8; i++) {
        // pinMode(_data_pins[i], OUTPUT);
        // digitalWrite(_data_pins[i], (value >> i) & 0x01);
    }

    pulseEnable();
}

void LCDSPI::spiSendOut() //SPI #############################
{

    m_CS = 0; /* Select SPI Chip MFRC522 */

    // MSB == 0 is for writing. LSB is not used in address. Datasheet section 8.1.2.3.
    //(void) m_SPI.write(reg & 0x7E);
    (void) m_SPI.write(_bitString);

    m_CS = 1; /* Release SPI Chip MFRC522 */



    /*

        Codigo velho

    //just in case you are using SPI for more then one device
    //set bitOrder, clockDivider and dataMode each time
    SPI.setClockDivider(_clockDivider);
    SPI.setBitOrder(_bitOrder);
    SPI.setDataMode(_dataMode);

    digitalWrite(_latchPin, LOW);
    SPI.transfer(_bitString);
    digitalWrite(_latchPin, HIGH);
      */

}