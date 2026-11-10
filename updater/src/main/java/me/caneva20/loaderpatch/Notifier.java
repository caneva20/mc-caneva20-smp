package me.caneva20.loaderpatch;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * The one notification the mod ever shows: a pending loader bump was applied
 * and the game must restart to pick it up. A Swing dialog when an UI is
 * available, nothing when there is none (the log records the pending restart).
 */
final class Notifier {
    private Notifier() {
    }

    /**
     * @return true when the player clicked "Relaunch now".
     */
    static boolean notify(String instanceName, String oldVersion, String newVersion,
            boolean allowRelaunch, Log log) {
        if (GraphicsEnvironment.isHeadless()) {
            log.info("headless: no popup possible; restart still pending (target "
                    + newVersion + ")");
            return false;
        }
        try {
            return showRelaunchDialog(instanceName, oldVersion, newVersion) && allowRelaunch;
        } catch (Throwable t) {
            log.error("popup failed: " + t + "; restart still pending (target "
                    + newVersion + ")");
            return false;
        }
    }

    private static boolean showRelaunchDialog(String instanceName, String oldVersion,
            String newVersion) throws Exception {
        AtomicBoolean relaunch = new AtomicBoolean(false);
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            JDialog d = new JDialog();
            d.setAlwaysOnTop(true);
            d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            d.setTitle("caneva20 SMP S2 - atualização pronta");
            d.setModal(true);
            d.setResizable(false);

            String from = esc(oldVersion == null ? "?" : oldVersion);
            JLabel text = new JLabel("<html><div style='width:340px;padding:6px'>"
                    + "<b>Atualização do modpack pronta.</b><br><br>"
                    + "NeoForge: <b>" + from + "</b> &#8594; <b>" + esc(newVersion) + "</b><br><br>"
                    + "Reinicie o jogo (aperte Play de novo) para aplicar "
                    + "a nova versão de &ldquo;" + esc(instanceName) + "&rdquo;.</div></html>");
            text.setHorizontalAlignment(SwingConstants.CENTER);

            JButton btnLater = new JButton("Jogar depois");
            JButton btnNow = new JButton("Reiniciar agora");
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
        return relaunch.get();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
