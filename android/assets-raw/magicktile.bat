rem This script will chop up images containing 16x16 tiles using http://www.graphicsmagick.org/
gm convert %1 -map %1 -crop 16x16 +adjoin %%03d.png
@echo off
rem magick -define colorspace:auto-grayscale=off %1 -crop 16x16 -depth 2 -colors 4 %1
