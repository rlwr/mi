# Unblacklist openmotif, it builds fine for us
PNBLACKLIST[openmotif] = ""

EXTRA_OECONF += " --disable-printing"
B="${S}"
