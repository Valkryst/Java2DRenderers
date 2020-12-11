## Foreward

While working on improvements for [VTerminal](https://github.com/Valkryst/VTerminal)
and various unreleased Java2D projects, I came across ["Blazing-fast Java2D Rendering"](https://tbrindus.ca/blazing-fast-java2d/)
which led me to the creation of the _*Renderer_ classes in this repository.

From what I recall, I originally began by copying-over all of the [renderer classes](https://github.com/Xyene/Nitrous-Emulator/tree/master/src/main/java/nitrous/renderer)
from [Xyene's](https://github.com/Xyene) [Nitrous-Emulator](https://github.com/Xyene/Nitrous-Emulator).
It took some time to understand this original codebase, but I took my time and
eventually got to a point where I felt comfortable working with the code.

Now that I was comfortable with his code, I took to reworking it in a way that
would be a bit easier for me to implement and work with in various other
projects.

Much of the code is the same as Xyene originally wrote it. My major changes are
in the naming of classes and the addition of documentation, convenience
methods, as well as minor code refactoring.

Although these renderers, at least the ones that I could test on my available
machines, did work reasonably well, I didn't encounter the massive performance
improvements that Xyene's original blog post described.

It could be my specific use-cases, which led to disappointing results, but this
project ultimately led me to believe that significant performance increases can
be found by optimizing your use of the existing Swing classes and working to
better understand the graphics pipeline.

If you're inclined to continue working on this project, please feel free to
submit a PR or to fork the repository. Ensure that you give credit to Xyene for
his original implementation, where applicable. I don't require any attribution
for my changes to his code, but it is appreciated.

## Usage

Before you can properly use any of the renderers in another project, you must
update the base `Renderer` class and make adjustments in the
`blitBufferToSurface` and `getBufferGraphics2D` functions. Additional
adjustments can be made in the `applyRenderHints` function, but they are not
required.

You can test the renderers by modifying the `com.valkryst.Java2DRenderers.Driver` class in the
`src/test/java/com.valkryst.Java2DRenderers` directory.

## Notes

### Compiling

To compile the renderer classes, you may need to add the following flags in your
call to `javac`.

```shell
--add-exports java.desktop/java.awt.peer=ALL-UNNAMED
--add-exports java.desktop/sun.java2d=ALL-UNNAMED
--add-exports java.desktop/sun.java2d.pipe.hw=ALL-UNNAMED
--add-exports java.desktop/sun.java2d.d3d=ALL-UNNAMED
```

If you encounter the `exporting a package from system module java.desktop is
not allowed with --release` exception, then you will need to uncheck the `Use
'--release' option for cross-compilation (Java 9 and later)` option in the
`Build, Execution, Deployment > Compiler > Java Compiler` settings view.

### Running

If you encounter the `An illegal reflective access operation has occurred`
exception, then you will need to run your program with the
`--illegal-access=permit` argument.