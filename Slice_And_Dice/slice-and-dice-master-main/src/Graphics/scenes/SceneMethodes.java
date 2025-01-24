package Graphics.scenes;

import java.awt.Graphics;

/* BUT
 * Déclare un ensemble de méthodes pour gérer divers événements liés à la souris et rendre des graphiques. 
 * Chaque scène peut ensuite gérer sa logique de rendu spécifique et ses événements liés à la souris en 
 * implémentant l'interface SceneMethodes.
 */
public interface SceneMethodes{
    
    public void render(Graphics g);
    public void mouseClicked(int x, int y);
    public void mouseMoved(int x, int y);
    public void mousePressed(int x, int y);
    public void mouseReleased(int x, int y);
    public void mouseDragged(int x, int y);
}
