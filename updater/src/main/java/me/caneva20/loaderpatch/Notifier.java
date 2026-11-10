package me.caneva20.loaderpatch;

import java.awt.GraphicsEnvironment;
import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/** The one popup the mod ever shows: clearing that pending loader is done. */
final class Notifier {
    private Notifier() {
    }

    /**
     * @return true if the player clicked "Relaunch now".
     */
    static boolean showRelaunchDialog(String instanceName, String oldVersion, String newVersion, Log log) {
        if (GraphicsEnvironment.isHeadless()) {
            log.info("headless environment - skipping popup");
            return false;
        }
        AtomicBoolean relaunch = new AtomicBoolean(false);
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                JDialog d = new JDialog();
                d.setAlwaysOnTop(true);
                d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                d.setTitle("Caneva20 SMP S2 - modpack updated");
                d.setModal(true);
                d.setResizable(false);

                JLabel text = new JLabel("<html><div style='width:340px;padding:6px'>"
                        + "<b>Modpack loader updated automatically.</b><br><br>"
                        + "NeoForge version: <b>" + esc(oldVersion) + "</b> &#8594; <b>" + esc(newVersion) + "</b><br><br>"
                        + "SMP option: relaunch &ldquo;" + esc(instanceName) + "&rdquo; to play on the "
                        + "updated version. (Choose &lsquo;Play later&rsquo; if you prefer.)</div></html>");
                text.setHorizontalAlignment(SwingConstants.CENTER);

                JButton btnLater = new JButton("Play later");
                JButton btnNow = new JButton("Relaunch now");
                btnNow.addActionListener(e -> {
                    relaunch.set(true);
                    d.dispose();
                });
                btnLater.addActionListener(e -> d.dispose());

                JPanel buttons = new JPanel();
                buttons.add(btnLater);
                buttons.add(btnNow);

                d.getContentPane().setLayout(new BorderLayout(4, 4));
                d.getContentPane().add(text, BorderLayout.CENTER);
                d.getContentPane().add(buttons, BorderLayout.SOUTH);
                d.getRootPane().setDefaultButton(btnNow);
                d.pack();
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            });
        } catch (Throwable t) {
            log.error("popup failed (game continues normally): " + t);
            return false;
        }
        return relaunch.get();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
