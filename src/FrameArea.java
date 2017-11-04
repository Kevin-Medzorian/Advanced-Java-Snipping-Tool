
import java.awt.Color;
import javax.swing.JFrame;

/**
 * @author Kevin
 */
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
