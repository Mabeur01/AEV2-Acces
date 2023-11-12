package AEV2;

import javax.swing.SwingUtilities;


public class Principal {

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            Vista view = new Vista();
            Model model = new Model();
            Controlador controller = new Controlador(model, view);

            view.setController(controller);

            view.setVisible(true);
        });
    }

}
