IRClockExtreme
==============

IR Controllable Clock Application for Raspberry Pi.

## LIRC configuration

### /etc/modules

```
# /etc/modules: kernel modules to load at boot time.
#
# This file contains the names of kernel modules that should be loaded
# at boot time, one per line. Lines beginning with "#" are ignored.
# Parameters can be specified after the module name.

snd-bcm2835
lirc_dev
lirc_rpi gpio_in_pin=17 gpio_out_pin=18
```

### /etc/lirc/hardware.conf

```
# /etc/lirc/hardware.conf
#
# Arguments which will be used when launching lircd
LIRCD_ARGS="--uinput"

#Don't start lircmd even if there seems to be a good config file
#START_LIRCMD=false

#Don't start irexec, even if a good config file seems to exist.
#START_IREXEC=false

#Try to load appropriate kernel modules
LOAD_MODULES=true

# Run "lircd --driver=help" for a list of supported drivers.
DRIVER="default"
# usually /dev/lirc0 is the correct setting for systems using udev 
DEVICE="/dev/lirc0"
MODULES="lirc_rpi"

# Default configuration files for your hardware if any
LIRCD_CONF=""
LIRCMD_CONF=""
```

### /etc/lirc/lircd.conf

```
begin remote

  name  Apple
  bits            8
  flags SPACE_ENC|CONST_LENGTH
  eps            30
  aeps          100

  header       9096  4622
  one           527  1773
  zero          527   645
  ptrail        526
  repeat       9099  2327
  pre_data_bits   16
  pre_data       0x77E1
  post_data_bits  8
  post_data      0xD0
  gap          108873
  toggle_bit_mask 0x3000

      begin codes
          MENU                     0xC0
          CENTER                   0xA0
          TOP                      0x50
          BOTTOM                   0x30
          LEFT                     0x90
          RIGHT                    0x60
      end codes

end remote

begin remote

  name  Toshiba
  bits           16
  flags SPACE_ENC|CONST_LENGTH
  eps            30
  aeps          100

  header      10243  5083
  one           624  1947
  zero          624   661
  ptrail        626
  pre_data_bits   16
  pre_data       0xE730
  gap          123013
  toggle_bit_mask 0x3838

      begin codes
          FULL                     0xE817
          OFF                      0xD02F
          SMALL                    0xF00F
      end codes

end remote

begin remote

  name  IODATA
  bits           16
  flags SPACE_ENC|CONST_LENGTH
  eps            30
  aeps          100

  header       8936  4453
  one           555  1674
  zero          555   561
  ptrail        554
  pre_data_bits   24
  pre_data       0x11748
  gap          107588
  min_repeat      1
#  suppress_repeat 1
#  uncomment to suppress unwanted repeats
  toggle_bit_mask 0xC0C

      begin codes
          POWER                    0x004C
          CH1                      0x0800
          CH2                      0x040C
          CH3                      0x0C04
          CH4                      0x020A
          CH5                      0x0A02
          CH6                      0x060E
          CH7                      0x0E06
          CH8                      0x0109
          CH9                      0x0901
          CH10                     0x050D
          CH11                     0x0D05
          CH12                     0x030B
      end codes

end remote
```

### /etc/lirc/lircrc

```
begin
	remote = Apple
	prog = irexec
	button = MENU
	config = /opt/ircex/cmd.sh MENU
end

begin
	remote = Apple
	prog = irexec
	button = TOP
	config = /opt/ircex/cmd.sh UP
end

begin
	remote = Apple
	prog = irexec
	button = BOTTOM
	config = /opt/ircex/cmd.sh DOWN
end

begin
	remote = Apple
	prog = irexec
	button = CENTER
	config = /opt/ircex/cmd.sh ENTER
end

begin
	remote = Apple
	prog = irexec
	button = LEFT
	config = /opt/ircex/cmd.sh LEFT
end

begin
	remote = Apple
	prog = irexec
	button = RIGHT
	config = /opt/ircex/cmd.sh RIGHT
end
```

## IRClockExtreme configuration

### /opt/ircex/cmd.sh

```
#!/bin/sh

if [ $# -ne 1 ]; then
    echo "Usage: $0 <command>"
    exit 1
fi

#HOST="192.168.1.11"
HOST="localhost"
DIR="/opt/ircex/"
LOG="cmd.log"
EXE="python cmd.py"
#EXE="java -jar cmd.jar"

cd $DIR
echo "----------" >> $LOG
date >> $LOG
$EXE $1 $HOST >> $LOG
```

### /opt/ircex/cmd.{jar, py}

Choise commander implementation and modify cmd.sh.

cmd.py is located here:
https://github.com/mikan/IRClockExtreme/blob/master/cmd/src/main/python/cmd.py

Have fun!
