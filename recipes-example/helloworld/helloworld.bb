SUMMARY = "Simple Hello World application"
DESCRIPTION = "This recipe builds a simple Hello World C program from GitHub."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "virtual/libc"

SRC_URI = "git://github.com/mnokka/yocto-helloworld.git;protocol=https;branch=main"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"




do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE \
        -I${STAGING_INCDIR} -o ${S}/helloworld ${S}/helloworld.c
}



do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/helloworld ${D}${bindir}
}

