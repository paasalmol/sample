# Copyright (c) 2020 LG Electronics, Inc.

SUMMARY = "mqtt configuration manager"
AUTHOR = "piyush.asalmol"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "nodejs-native"
RDEPENDS_${PN} = "nodejs nodejs-module-webos-service"

WEBOS_VERSION = "1.0.1-2_2a284d47111e054cf1eb67f36185f4e9ddf00642"
PR = "r0"

# The same restriction as nodejs as nodejs itself added to RDEPENDS_${PN}
COMPATIBLE_MACHINE_armv4 = "(!.*armv4).*"
COMPATIBLE_MACHINE_armv5 = "(!.*armv5).*"
COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"

SRC_URI = "${WEBOS_IOT_GIT_REPO_COMPLETE}"
S = "${WORKDIR}/git"

inherit webos_component
inherit webos_cmake
inherit webos_system_bus
inherit webos_machine_impl_dep
inherit webos_submissions
inherit webos_public_repo
inherit webos_enhanced_submissions

def get_nodejs_arch(d):
    target_arch = d.getVar('TRANSLATED_TARGET_ARCH', True)

    if target_arch == "x86-64":
        target_arch = "x64"
    elif target_arch == "aarch64":
        target_arch = "arm64"
    elif target_arch == "powerpc":
        target_arch = "ppc"
    elif target_arch == "powerpc64":
        target_arch = "ppc64"
    elif (target_arch == "i486" or target_arch == "i586" or target_arch == "i686"):
        target_arch = "ia32"

    return target_arch

NPM_CACHE_DIR ?= "${WORKDIR}/npm_cache"
NPM_REGISTRY ?= "http://registry.npmjs.org/"
NPM_ARCH = "${@get_nodejs_arch(d)}"
NPM_INSTALL_FLAGS ?= "--production --without-ssl --insecure --no-optional --verbose"

do_compile() {
    cd ${S}
    # configure cache to be in working directory
    ${STAGING_BINDIR_NATIVE}/npm set cache ${NPM_CACHE_DIR}

    # clear local cache prior to each compile
    ${STAGING_BINDIR_NATIVE}/npm cache clear --force

    ${STAGING_BINDIR_NATIVE}/npm --registry=${NPM_REGISTRY} --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} ${NPM_INSTALL_FLAGS} install
}

FILES_${PN} += "${webos_servicesdir} ${webos_sysconfdir}"