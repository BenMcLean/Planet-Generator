For /R %%G IN (*.png) do convert "%%G" -type PaletteAlpha -remap palette.png png8:"%%G"
