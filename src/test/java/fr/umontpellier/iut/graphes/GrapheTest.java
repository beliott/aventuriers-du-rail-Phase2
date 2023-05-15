package fr.umontpellier.iut.graphes;


import fr.umontpellier.iut.rails.Route;
import fr.umontpellier.iut.rails.RouteMaritime;
import fr.umontpellier.iut.rails.data.Couleur;
import fr.umontpellier.iut.rails.data.Ville;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GrapheTest {
    private Graphe  graphe;

    @BeforeEach
    void setUp() {
        List<Arete> aretes = new ArrayList<>();
        aretes.add(new Arete(0, 1));
        aretes.add(new Arete(0, 3));
        aretes.add(new Arete(1, 2));
        aretes.add(new Arete(2, 3));
        aretes.add(new Arete(8, 42));
        graphe = new Graphe(aretes);
    }

    @Test
    void constructeur_2(){
        Graphe graphe1 = new Graphe();
        assertEquals(graphe1.nbAretes(), 0);
        assertEquals(graphe1.nbSommets(), 0);

    }

    @Test
    void constructeur_4(){
        Graphe induit = new Graphe(graphe, new HashSet<>(Arrays.asList(1, 2, 3)));
        assertEquals(induit.nbAretes(), 2); // 1,2 && 2,3
    }

    @Test
    void testNbAretes() {
        assertEquals(5, graphe.nbAretes());
    }

    @Test
    void ensembleSommet(){
        Set<Integer> sommets = new HashSet<>();
        sommets.add(0);
        sommets.add(1);
        sommets.add(2);
        sommets.add(3);
        sommets.add(8);
        sommets.add(42);
        assertEquals(sommets, graphe.ensembleSommets());
        assertEquals(6, graphe.ensembleSommets().size());
    }


    @Test
    void testContientSommet() {
        assertTrue(graphe.contientSommet(0));
        assertTrue(graphe.contientSommet(1));
        assertTrue(graphe.contientSommet(2));
        assertTrue(graphe.contientSommet(3));
        assertTrue(graphe.contientSommet(8));
        assertTrue(graphe.contientSommet(42));
        assertFalse(graphe.contientSommet(7));
    }

    @Test
    void testAjouterSommet() {
        int nbSommets = graphe.nbSommets();
        graphe.ajouterSommet(59);
        assertTrue(graphe.contientSommet(59));
        assertEquals(nbSommets + 1, graphe.nbSommets());
        graphe.ajouterSommet(59);
        assertEquals(nbSommets + 1, graphe.nbSommets());
    }

    @Test
    void testAjouterArete() {
        int nbAretes = graphe.nbAretes();
        graphe.ajouterArete(new Arete(0, 3));
        assertEquals(nbAretes, graphe.nbAretes());
        graphe.ajouterArete(new Arete(9, 439, null));
        assertEquals(nbAretes + 1, graphe.nbAretes());
        graphe.ajouterArete(new Arete(0, 3, new RouteMaritime(new Ville("Athina", true), new Ville("Marseille", true), Couleur.ROUGE, 2) {
        }));
        assertEquals(nbAretes + 2, graphe.nbAretes());
    }

    @Test
    void testSupprimerArete() {
        int nbAretes = graphe.nbAretes();
        graphe.supprimerArete(new Arete(0, 3));
        assertEquals(nbAretes - 1, graphe.nbAretes());
        graphe.supprimerArete(new Arete(0, 3));
        assertEquals(nbAretes - 1, graphe.nbAretes());
        graphe.supprimerArete(new Arete(0, 3, null));
        assertEquals(nbAretes - 1, graphe.nbAretes());
    }

    @Test
    void testSupprimerSommet() {
        int nbSommets = graphe.nbSommets();
        int nbAretes = graphe.nbAretes();
        graphe.supprimerSommet(42);
        assertEquals(nbSommets - 1, graphe.nbSommets());
        assertEquals(nbAretes - 1, graphe.nbAretes());
        graphe.supprimerSommet(2);
        assertEquals(nbSommets - 2, graphe.nbSommets());
        assertEquals(nbAretes - 3, graphe.nbAretes());
    }

    @Test
    void testExisteArete() {
        assertTrue(graphe.existeArete(new Arete(0, 1)));
        assertTrue(graphe.existeArete(new Arete(0, 3)));
        assertTrue(graphe.existeArete(new Arete(1, 2)));
        assertTrue(graphe.existeArete(new Arete(2, 3)));
        assertTrue(graphe.existeArete(new Arete(8, 42)));
        assertFalse(graphe.existeArete(new Arete(0, 2)));
        assertFalse(graphe.existeArete(new Arete(0, 4)));
        assertFalse(graphe.existeArete(new Arete(1, 3)));
        assertFalse(graphe.existeArete(new Arete(2, 4)));
        assertFalse(graphe.existeArete(new Arete(8, 43)));
    }

    @Test
    void testGetVoisins() {
        assertEquals(graphe.getVoisins(0), new HashSet<Integer>(Arrays.asList(1, 3)));
        assertEquals(graphe.getVoisins(1), new HashSet<Integer>(Arrays.asList(0, 2)));
        assertEquals(graphe.getVoisins(2), new HashSet<Integer>(Arrays.asList(1, 3)));
        assertEquals(graphe.getVoisins(3), new HashSet<Integer>(Arrays.asList(0, 2)));
        assertEquals(graphe.getVoisins(8), new HashSet<Integer>(List.of(42)));
        assertEquals(graphe.getVoisins(42), new HashSet<Integer>(List.of(8)));

    }

    @Test
    void testEstSimple(){
        assertTrue(graphe.estSimple());
        graphe.ajouterArete(new Arete(0, 0));
        assertFalse(graphe.estSimple());
    }

    /*
    @Test
    void testAretePourLaCulture(){
        Arete a = new Arete(0, 1);
        int nbAvant = graphe.nbAretes();
        graphe.ajouterArete(a);
        int nbApres = graphe.nbAretes();
        assertEquals(nbAvant, nbApres);
    }

     */

    @Test
    public void test_degreMax(){
        assertEquals(graphe.degreMax(), 2);
        graphe.ajouterArete(new Arete(0, 42));
        assertEquals(graphe.degreMax(), 3);
        graphe.supprimerSommet(0);
        assertEquals(graphe.degreMax(), 2);
    }

    @Test
    public void test_estComplet(){
        Graphe complet = new Graphe(4);
        complet.ajouterArete(new Arete(0,1));
        complet.ajouterArete(new Arete(0,2));
        complet.ajouterArete(new Arete(0,3));
        complet.ajouterArete(new Arete(1,2));
        complet.ajouterArete(new Arete(1,3));
        complet.ajouterArete(new Arete(2,3));
        assertTrue(complet.estComplet());
        assertFalse(graphe.estComplet());

    }

    @Test
    public void test_estUneChaine(){
        Graphe gVide = new Graphe();
        assertTrue(gVide.estUneChaine());
        assertFalse(graphe.estUneChaine());
        Graphe gChaine = new Graphe(graphe, Set.of(0, 1, 2, 3));
        assertFalse(graphe.estUneChaine()); // encore un cycle pour le moment
        gChaine.supprimerArete(new Arete(2, 3));
        assertTrue(gChaine.estUneChaine());
        // test de consigne
        Graphe chaineOrdre10 = new Graphe(10);
        for (int i = 0; i < 10; i++) {
            if (i != 9){
                Arete a = new Arete(i, i+1);
                chaineOrdre10.ajouterArete(a);
            }
        }
        assertTrue(chaineOrdre10.estUneChaine());
        chaineOrdre10.ajouterArete(new Arete(9, 0));
        assertFalse(chaineOrdre10.estUneChaine());
    }
}