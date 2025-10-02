SUMMARY = "Custom Qt5 image for Raspberry Pi4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Perustuu core-image-baseen (sis. enemmän kuin minimal)
inherit core-image

IMAGE_FEATURES += "splash package-management"

IMAGE_INSTALL:append = " packagegroup-core-ssh-openssh"

# Qt5 packagegroupit ja peruskirjastot
IMAGE_INSTALL:append = " \
    packagegroup-qt5-toolchain-target \
    qtbase \
    qtbase-plugins \
    qtdeclarative \
    qtquickcontrols2 \
    helloworld \
    hellouiworld \
"

# Lisätään myös oma Qt-ohjelma (kun teet reseptin sille)
#IMAGE_INSTALL:append = " hellouiworld "
#IMAGE_INSTALL:append = " helloworld hellouiworld"
