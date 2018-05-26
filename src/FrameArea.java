
/*
File:   FrameArea.java 
Copyright 2018, Kevin Medzorian

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of 
the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.awt.Color;
import javax.swing.JFrame;

public class FrameArea extends JFrame {

    private static final float DEFAULT_ALPHA = 0.8f;

    public FrameArea() {
        super();
        SetFrameSize(500, 500);
        SetFrameAlpha(DEFAULT_ALPHA);
        SetFramePosition(500, 500);

        setVisible(true);
        setAlwaysOnTop(true);
    }

    public FrameArea(float f) {
        super();
        SetFrameSize(500, 500);
        SetFrameAlpha(f);
        SetFramePosition(500, 500);

        setAlwaysOnTop(true);

    }

    public FrameArea(int x, int y) {
        super();

        SetFrameSize(500, 500);
        SetFrameAlpha(DEFAULT_ALPHA);
        SetFramePosition(x, y);

        setAlwaysOnTop(true);
    }

    void SetFrameAlpha(float f) {
        Color col = new Color(1.0f, 1.0f, 1.0f, f);
        getContentPane().setBackground(col);

        setUndecorated(true);

        setOpacity(f);
    }

    void SetFramePosition(int x, int y) {
        setLocation(x, y);
    }

    void SetFrameSize(int x, int y) {
        setSize(x, y);
    }
}
