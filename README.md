# Yocto_building_RaspberryPi4

## Purpose

Build a Yocto image for Raspberry Pi 4 with custom login texts via a separate layer. All modifications are contained in meta-yocto-building-raspberrypi4, keeping official layers untouched. Image has own login text added.
Also helloworld is fetched from GitHub repo and added to image ( see meta-yocto-building-raspberrypi4/recipes-example/helloworld/helloworld.bb)

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
```


### Main directory tree
```
YOCTO
├── meta-openembedded
├── meta-raspberrypi
├── meta-yocto-building-raspberrypi4
├── poky
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
bitbake-layers add-layer ../meta-yocto-building-raspberrypi4
```

Important: meta-yocto-building-raspberrypi4 contains all custom modifications, including login text (/etc/issue) and machine configuration.

### Build the image

```bash
cd BUILD-PI4
bitbake core-image-minimal 
```

Build images appear in BUILD-PI4/tmp/deploy/images/raspberrypi4-64

### Enable and rebuild the image

Edit conf/local.conf inside your build directory and add:


```bash
IMAGE_INSTALL:append = " helloworld"
```
```bash
bitbake core-image-minimal
```

Helloworld can be executed in Pi4 after flashing: helloworld

### Flashing with BalenEtcher


https://www.balena.io/etcher/

Use image:

BUILD-PI4/tmp/deploy/images/raspberrypi4-64/core-image-minimal-raspberrypi4-64.wic.bz2



### RS232

Image has RS232 debug texts and possibility to login via RS232 activated. For debugging Linux can use minicom as a terminal

```bash
minicom -D /dev/ttyUSB0
```

A special USB-to-Raspberry Pi UART cable is required



### Example login screen

![Custom login text](images/login.jpg)


