# Yocto_building_RaspberryPi4

## Purpose

Build a Yocto image for Raspberry Pi 4 with custom login texts via a separate layer. All modifications are contained in meta-yocto-building-raspberrypi4, keeping official layers untouched. 

1) Image has own login text added.

2) C helloworld is fetched from GitHub repo and added to image ( see meta-yocto-building-raspberrypi4/recipes-example/helloworld/helloworld.bb)

3) Both Pi4 and qemu builds (sharing build cache and downloads) 

4) Pi4 build includes Qt5 C++ hellouiworld

### Prepare YOCTO directory

```bash
mkdir ~/YOCTO
cd ~/YOCTO
```


### Clone needed repos for layers

```bash
#Own custom layer
git clone https://github.com/mnokka/meta-yocto-building-raspberrypi4.git

#Core Yocto and Raspberry Pi layers (dunfell branch) 
git clone -b dunfell https://git.yoctoproject.org/poky.git
git clone git://git.yoctoproject.org/meta-raspberrypi -b dunfell
git clone git://git.openembedded.org/meta-openembedded -b dunfell
git clone https://github.com/meta-qt5.git
</pre>
```


### Main directory tree

```bash 
YOCTO/
├── BUILD-PI4
├── BUILD-QEMU
├── build-qemu-arm64
├── downloads
├── flashing
├── meta-openembedded
├── meta-qt5
├── meta-raspberrypi
├── meta-yocto-building-raspberrypi4
├── poky
└── sstate-cache
```



### Custom layer structure

```
meta-yocto-building-raspberrypi4
├── conf/
│   ├── layer.conf                # Layer basic configuration
│   └── machine/
│       └── raspberrypi4-64.conf  # Build and machine settings
└── recipes-core/
    └── base-files/
        ├── base-files_%.bbappend # bbappend replaces /etc/issue
        └── files/
            └── issue             # own login prompt text
└── recipes-example/
    └── helloworld/
        └── helloworld.bb       # Example program recipe

└──recipes-qt
    └── hellouiworld
        ├── files
        │   ├── hellouiworld.pro
        │   ├── LICENSE
        │   ├── main.cpp # Qt5 SW
        │   └── toimiva.main.cpp 
        └── hellouiworld.bb # Qt5 SW recipe

```


### Create/Set existing build directory:

source poky/oe-init-build-env BUILD-PI4

### Create layers:

```bash
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-openembedded/meta-networking
bitbake-layers add-layer ../meta-openembedded/meta-multimedia
bitbake-layers add-layer ../meta-raspberrypi
bitbake-layers add-layer ../meta-qt5
bitbake-layers add-layer ../meta-yocto-building-raspberrypi4
```

Important: meta-yocto-building-raspberrypi4 contains all custom modifications, including login text (/etc/issue) and machine configuration. Qt5 SW code, C code is fetched from github during building


### local conf changes

Some changes added to build directory conf/local.cof file (some can be layered later)
and are author machine (directory) spesific.

bblayers.conf

```bash

POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /media/mika/YOCTO_T7/YOCTO/poky/meta \
  /media/mika/YOCTO_T7/YOCTO/poky/meta-poky \
  /media/mika/YOCTO_T7/YOCTO/poky/meta-yocto-bsp \
  /media/mika/YOCTO_T7/YOCTO/meta-openembedded/meta-oe \
  /media/mika/YOCTO_T7/YOCTO/meta-openembedded/meta-python \
  /media/mika/YOCTO_T7/YOCTO/meta-openembedded/meta-networking \
  /media/mika/YOCTO_T7/YOCTO/meta-openembedded/meta-multimedia \
  /media/mika/YOCTO_T7/YOCTO/meta-raspberrypi \
  /media/mika/YOCTO_T7/YOCTO/meta-qt5 \
  /media/mika/YOCTO_T7/YOCTO/meta-yocto-building-raspberrypi4 \

  "
```

local.conf

#### local_conf/pi4 build:

```bash
MACHINE = "raspberrypi4-64"
...
ENABLE_UART = "1"

EXTRA_IMAGE_FEATURES += "debug-tweaks"
IMAGE_INSTALL:append = " packagegroup-core-ssh-openssh helloworld hellouiworld"
PACKAGECONFIG:append:pn-qtbase = " eglfs"
BB_NUMBER_THREADS ?= "4"      
PARALLEL_MAKE ?= "-j 4"
DL_DIR ?= "/media/mika/YOCTO_T7/YOCTO/downloads"
SSTATE_DIR ?= "/media/mika/YOCTO_T7/YOCTO/sstate-cache"
```

#### local_conf/QMEU build:

```bash
MACHINE ?= "qemuarm"
...
BB_NUMBER_THREADS ?= "4"
PARALLEL_MAKE ?= "-j 4"
EXTRA_IMAGE_FEATURES += "debug-tweaks"
IMAGE_INSTALL:append = " packagegroup-core-ssh-openssh helloworld" # hellouiworld"
#PACKAGECONFIG:append:pn-qtbase = " eglfs"
DL_DIR ?= "/media/mika/YOCTO_T7/YOCTO/downloads"
SSTATE_DIR ?= "/media/mika/YOCTO_T7/YOCTO/sstate-cache"
```

### Build the P4 image with C helloworld

```bash
cd BUILD-PI4
bitbake core-image-minimal 
```

Built image appear in BUILD-PI4/tmp/deploy/images/raspberrypi4-64


### Qemu build

 Only difference is the build directory setting and build/run commands

```bash
 source oe-init-build-env ../BUILD-QEMU
 bitbake core-image-minimal
 runqemu qemuarm core-image-minimal
```

### Qt5 hellouiworld 

Recipe recipes-qt/helloworld/helloworld.bb defines SW.
It references existing MIT licence. Due -platform eglfs usage, Qt writes direct OpenGL ES
(no X11/Wayland). This limits some graffic operations in code.

Image recipe recipes-core/images/my-qt5-image.bb defines image.

Build: 

```bash
source poky/oe-init-build-env BUILD-PI4
bitbake my-qt5-image 
```

Image (example) can be found in BUILD-PI4/tmp/deploy/images/raspberrypi4-64/[my-qt5-image-raspberrypi4-64-20251001144600.rootfs.tar.bz2
copy it to "other place" (to get rid of links) and extract content

### Flashing with BalenEtcher


https://www.balena.io/etcher/

Use image:

BUILD-PI4/tmp/deploy/images/raspberrypi4-64/core-image-minimal-raspberrypi4-64.wic.bz2
or
"other_place"/my-qt5-image-raspberrypi4-64-20251001050532.rootfs.wic


### Executing in Pi4 shell

```bash
helloworld (Pi4 image and QEMU image)
hellouiworld -platform eglfs (qt image)
```

With Qt5 degugs activated

```bash
export QT_QPA_EGLFS_DEBUG=1
./hellouiworld -platform eglfs
```

### RS232

Image has RS232 debug texts and possibility to login via RS232 activated. For debugging Linux can use minicom as a terminal

```bash
minicom -D /dev/ttyUSB0
```

A special USB-to-Raspberry Pi UART cable is required



### Example login screen

![Custom login text](images/login.jpg)

![Qt HelloWorld](images/helloqt.jpg)

![C HelloWorld](images/helloc.png)
