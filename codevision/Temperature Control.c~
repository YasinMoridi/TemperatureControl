/*****************************************************
Project : Temperature Control System
Version : 2.3 (Interrupt-driven UART + Threshold Control)
Author  : Yasin Moridi
*****************************************************/

#include <mega16.h>
#include <delay.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#pragma warn-

#define LCD_RS     PORTC.0
#define LCD_RW     PORTC.1
#define LCD_EN     PORTC.2
#define LCD_D4     PORTC.4
#define LCD_D5     PORTC.5
#define LCD_D6     PORTC.6
#define LCD_D7     PORTC.7

#define LCD_RS_DIR DDRC.0
#define LCD_RW_DIR DDRC.1
#define LCD_EN_DIR DDRC.2
#define LCD_D4_DIR DDRC.4
#define LCD_D5_DIR DDRC.5
#define LCD_D6_DIR DDRC.6
#define LCD_D7_DIR DDRC.7

#define FAN_PIN    PORTB.0
#define FAN_DIR    DDRB.0

#define DEFAULT_THRESHOLD 30
#define UBRR_VAL 51
#define RX_BUF_SIZE 32

volatile char rx_buffer[RX_BUF_SIZE];
volatile unsigned char rx_index = 0;
volatile unsigned char rx_ready = 0;

int temp_threshold = DEFAULT_THRESHOLD;

void uart_init(void) {
    UBRRH = 0;
    UBRRL = UBRR_VAL;
    UCSRB = (1 << RXCIE) | (1 << TXEN) | (1 << RXEN);
    UCSRC = (1 << URSEL) | (1 << UCSZ1) | (1 << UCSZ0);
}

interrupt [USART_RXC] void usart_rx_isr(void) {
    char c = UDR;
    if (rx_ready) return;
    
    if (c == '\n' || c == '\r') {
        if (rx_index > 0) {
            rx_buffer[rx_index] = '\0';
            rx_ready = 1;
            rx_index = 0;
        }
    } else {
        if (rx_index < RX_BUF_SIZE - 1) {
            rx_buffer[rx_index++] = c;
        } else {
            rx_index = 0;
        }
    }
}

void uart_send_char(char c) {
    while (!(UCSRA & (1 << UDRE)));
    UDR = c;
}

void uart_send_string(char *str) {
    while (*str) uart_send_char(*str++);
}

void uart_send_data(int temperature, unsigned char fan_on) {
    char buffer[56];
    sprintf(buffer, "{\"temp\":%d,\"fan\":%d,\"threshold\":%d}\r\n",
            temperature, fan_on ? 1 : 0, temp_threshold);
    uart_send_string(buffer);
}

void parse_command(char *str) {
    char ack[40];
    char *ptr;
    int val;
    
    ptr = strstr(str, "threshold");
    if (ptr != NULL) {
        ptr = strchr(ptr, ':');
        if (ptr != NULL) {
            ptr++;
            val = atoi(ptr);
            
            if (val >= 0 && val <= 100) {
                temp_threshold = val;
                sprintf(ack, "{\"ack\":\"threshold\",\"value\":%d}\r\n", temp_threshold);
                uart_send_string(ack);
            }
        }
    }
}

void lcd_pulse_enable(void) {
    LCD_EN = 1; delay_us(1); LCD_EN = 0; delay_us(100);
}
void lcd_send_nibble(unsigned char nibble) {
    LCD_D4 = (nibble >> 0) & 1; LCD_D5 = (nibble >> 1) & 1;
    LCD_D6 = (nibble >> 2) & 1; LCD_D7 = (nibble >> 3) & 1;
    lcd_pulse_enable();
}
void lcd_send_byte(unsigned char byte) {
    lcd_send_nibble(byte >> 4); lcd_send_nibble(byte & 0x0F);
}
void lcd_command(unsigned char cmd) {
    LCD_RS = 0; LCD_RW = 0; lcd_send_byte(cmd); delay_ms(2);
}
void lcd_data(unsigned char data) {
    LCD_RS = 1; LCD_RW = 0; lcd_send_byte(data); delay_ms(1);
}
void lcd_init(void) {
    LCD_RS_DIR = 1; LCD_RW_DIR = 1; LCD_EN_DIR = 1;
    LCD_D4_DIR = 1; LCD_D5_DIR = 1; LCD_D6_DIR = 1; LCD_D7_DIR = 1;
    delay_ms(50); LCD_RS = 0; LCD_RW = 0; LCD_EN = 0;
    lcd_send_nibble(0x03); delay_ms(5);
    lcd_send_nibble(0x03); delay_us(150);
    lcd_send_nibble(0x03); lcd_send_nibble(0x02);
    lcd_command(0x28); lcd_command(0x0C); lcd_command(0x06); lcd_command(0x01); delay_ms(2);
}
void lcd_clear(void) { lcd_command(0x01); delay_ms(2); }
void lcd_gotoxy(unsigned char x, unsigned char y) {
    unsigned char address = (y == 0) ? (0x80 + x) : (0xC0 + x);
    lcd_command(address);
}
void lcd_puts(char *str) { while (*str) lcd_data(*str++); }

void adc_init(void) {
    DDRA.0 = 0; PORTA.0 = 0;
    ADMUX = (1 << REFS0);
    ADCSRA = (1 << ADEN) | (1 << ADPS2) | (1 << ADPS1);
    ADCSRA |= (1 << ADSC);
    while (ADCSRA & (1 << ADSC));
}
unsigned int read_adc(unsigned char channel) {
    unsigned long sum = 0; unsigned char i;
    ADMUX = (ADMUX & 0xE0) | (channel & 0x07); delay_ms(10);
    for (i = 0; i < 10; i++) {
        ADCSRA |= (1 << ADSC); while (ADCSRA & (1 << ADSC));
        sum += ADCW; delay_ms(2);
    }
    return (unsigned int)(sum / 10);
}
int get_temperature(void) {
    unsigned int adc_val = read_adc(0);
    unsigned long voltage_mv = ((unsigned long)adc_val * 5000UL) / 1023UL;
    return (int)(voltage_mv / 10);
}
void display_temperature(int temperature, unsigned char fan_on) {
    char buffer[16];
    lcd_gotoxy(0, 0); sprintf(buffer, "T:%3dC Th:%3dC", temperature, temp_threshold); lcd_puts(buffer);
    lcd_gotoxy(0, 1);
    if (fan_on) lcd_puts("Fan: ON         ");
    else lcd_puts("Fan: OFF        ");
}
void init_system(void) {
    FAN_DIR = 1; FAN_PIN = 0;
    uart_init(); adc_init(); lcd_init();
    lcd_clear(); lcd_gotoxy(0, 0); lcd_puts("Temp Control");
    lcd_gotoxy(0, 1); lcd_puts("System Ready"); delay_ms(2000); lcd_clear();
}

void main(void) {
    int temperature;
    unsigned char fan_on;
    unsigned int timer_counter = 0; 

    init_system();
    
    #asm("sei")

    while (1) {
       
        if (rx_ready) {
            parse_command((char*)rx_buffer);
            rx_ready = 0;
        }

        if (timer_counter >= 100) {
            temperature = get_temperature();
            fan_on = (temperature > temp_threshold) ? 1 : 0;
            FAN_PIN = fan_on;
            display_temperature(temperature, fan_on);
            uart_send_data(temperature, fan_on);
            timer_counter = 0;
        }

        delay_ms(10); 
        timer_counter++;
    }
}