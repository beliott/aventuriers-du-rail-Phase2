package fr.umontpellier.iut.graphes;

import fr.umontpellier.iut.rails.Route;

import java.util.*;

/**
 * (Multi) Graphe non-orienté pondéré. Le poids de chaque arête correspond à la longueur de la route correspondante.
 * Pour une paire de sommets fixée {i,j}, il est possible d'avoir plusieurs arêtes
 * d'extrémités i et j et de longueur identique, du moment que leurs routes sont différentes.
 * Par exemple, il est possible d'avoir les deux arêtes suivantes dans le graphe :
 * Arete a1 = new Arete(i,j,new RouteTerrestre(villes.get("Lima"), villes.get("Valparaiso"), Couleur.GRIS, 2))
 * et
 * Arete a2 = new Arete(i,j,new RouteTerrestre(villes.get("Lima"), villes.get("Valparaiso"), Couleur.GRIS, 2))
 * Dans cet exemple (issus du jeu), a1 et a2 sont deux arêtes différentes, même si leurs routes sont très similaires
 * (seul l'attribut nom est différent).
 */
public class Graphe {

    /**
     * Liste d'incidences :
     * mapAretes.get(1) donne l'ensemble d'arêtes incidentes au sommet dont l'identifiant est 1
     * Si mapAretes.get(u) contient l'arête {u,v} alors, mapAretes.get(v) contient aussi cette arête
     */
    private Map<Integer, HashSet<Arete>> mapAretes;


    /**
     * Construit un graphe à n sommets 0..n-1 sans arêtes
     */
    public Graphe(int n) {
        this.mapAretes = new HashMap<>();
        for (int i = 0; i < n; i++) {
            this.mapAretes.put(i, new HashSet<>());
        }
    }

    /**
     * Construit un graphe vide
     */
    public Graphe() {
        this.mapAretes = new HashMap<>();
    }

    /**
     * Construit un graphe à partir d'une collection d'arêtes.
     *
     * @param aretes la collection d'arêtes
     */
    public Graphe(Collection<Arete> aretes) {
        this.mapAretes = new HashMap<>();
        for (Arete a : aretes) {
            if (!mapAretes.containsKey(a.i())){
                mapAretes.put(a.i(), new HashSet<>());
            }
            mapAretes.get(a.i()).add(a);
            if (!mapAretes.containsKey(a.j())){
                mapAretes.put(a.j(), new HashSet<>());
            }
            mapAretes.get(a.j()).add(a);
        }
    }

    /**
     * À partir d'un graphe donné, construit un sous-graphe induit
     * par un ensemble de sommets, sans modifier le graphe donné
     *
     * @param graphe le graphe à partir duquel on construit le sous-graphe
     * @param X      l'ensemble de sommets qui définissent le sous-graphe
     *               prérequis : X inclus dans V()
     */
    public Graphe(Graphe graphe, Set<Integer> X) {
        this.mapAretes = new HashMap<>();
        for (Integer i : X) {
            HashSet<Arete> tempAretes = graphe.mapAretes.get(i);
            for (Arete a : tempAretes) {
                if (X.contains(a.i()) && X.contains(a.j())){ // si l'arete relie deux points presents dans X
                    if (!this.mapAretes.containsKey(a.i())){
                        this.mapAretes.put(a.i(), new HashSet<>());
                    }
                    mapAretes.get(a.i()).add(a);
                    if (!mapAretes.containsKey(a.j())){
                        mapAretes.put(a.j(), new HashSet<>());
                    }
                    mapAretes.get(a.j()).add(a); // pas besoin de verif de pas ajt si deja present car HashSet le fait
                }
            }
        }
    }

    /**
     * @return l'ensemble de sommets du graphe
     */
    public Set<Integer> ensembleSommets() {
        return new HashSet<>(mapAretes.keySet()); // TODO: verif si il faut return une copie ou le vrai ensemble
    }

    /**
     * @return l'ordre du graphe (le nombre de sommets)
     */
    public int nbSommets() {
        return mapAretes.size();
    }

    /**
     * @return le nombre d'arêtes du graphe (ne pas oublier que this est un multigraphe : si plusieurs arêtes sont présentes entre un même coupe de sommets {i,j}, il faut
     * toutes les compter)
     */
    public int nbAretes() {
        int sommeDegres = 0;
        for (Integer i: mapAretes.keySet()) {
            sommeDegres += mapAretes.get(i).size();
        }
        return sommeDegres / 2;
    }


    public boolean contientSommet(Integer v) {
        return mapAretes.containsKey(v);
    }

    /**
     * Ajoute un sommet au graphe s'il n'est pas déjà présent
     *
     * @param v le sommet à ajouter
     */
    public void ajouterSommet(Integer v) {
        if (!this.mapAretes.containsKey(v)){
            this.mapAretes.put(v, new HashSet<>());
        }
    }

    /**
     * Ajoute une arête au graphe si elle n'est pas déjà présente
     *
     * @param a l'arête à ajouter. Si les 2 sommets {i,j} de a ne sont pas dans l'ensemble,
     *          alors les sommets sont automatiquement ajoutés à l'ensemble de sommets du graphe
     */
    public void ajouterArete(Arete a) {
        if (!mapAretes.containsKey(a.i())){
            ajouterSommet(a.i());
        }
        if (!mapAretes.containsKey(a.j())){
            ajouterSommet(a.j());
        }
        mapAretes.get(a.i()).add(a);
        mapAretes.get(a.j()).add(a);
    }

    /**
     * Supprime une arête du graphe si elle est présente, sinon ne fait rien
     *
     * @param a arête à supprimer
     *
     */
    public void supprimerArete(Arete a) {
        if (this.mapAretes.containsKey(a.i()) && this.mapAretes.containsKey(a.j())){ // si sommets de l'arete existe
            if (this.mapAretes.get(a.i()).contains(a) && this.mapAretes.get(a.j()).contains(a)){ // si contient a
                this.mapAretes.get(a.i()).remove(a);
                this.mapAretes.get(a.j()).remove(a);
            }
        }
    }

    /**
     * @param a l'arête dont on veut tester l'existence
     * @return true si a est présente dans le graphe
     */
    public boolean existeArete(Arete a) {
        for (Integer i :this.mapAretes.keySet()) {
            HashSet<Arete> iAretes = new HashSet<>(mapAretes.get(i));
            if (iAretes.contains(a))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer v : mapAretes.keySet()) {
            sb.append("sommet").append(v).append(" : ").append(mapAretes.get(v)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Retourne l'ensemble des sommets voisins d'un sommet donné.
     * Si le sommet n'existe pas, l'ensemble retourné est vide.
     *
     * @param v l'identifiant du sommet dont on veut le voisinage
     */
    public Set<Integer> getVoisins(int v) {
        HashSet<Integer> voisins =new HashSet<>();
        if (mapAretes.containsKey(v)){
            for (Arete a : mapAretes.get(v)) {
                voisins.add(a.getAutreSommet(v));
            }
        }
       return voisins;
    }

    /**
     * Supprime un sommet du graphe, ainsi que toutes les arêtes incidentes à ce sommet
     *
     * @param v le sommet à supprimer
     */
    public void supprimerSommet(int v) {
        while (!this.mapAretes.get(v).isEmpty()){
            Arete a = this.mapAretes.get(v).iterator().next();
            supprimerArete(a);
        }
        this.mapAretes.keySet().remove(v);
    }

    public int degre(int v) {
        return this.mapAretes.get(v).size();
    }

    /**
     *
     * @return le degré max, et Integer.Min_VALUE si le graphe est vide
     */
    public int degreMax(){
        if (this.nbAretes() == 0){  // par rapport aux aretes car peut avoir sommets mais vides
            return Integer.MIN_VALUE;
        }
        int max = 0;
        for (Integer i: this.mapAretes.keySet()) {
            if (degre(i) > max){
                max = degre(i);
            }
        }
        return max;
    }

    /**
     *
     * @return true si on ne trouve pas plusieurs aretes reliant deux memes sommets. (pour tous les sommets du graphe)
     */
    public boolean estSimple(){
        for (Integer i : this.mapAretes.keySet()) {
            HashSet<Arete> temp = new HashSet<>(this.mapAretes.get(i));
            for (Arete a : temp) {
                int sommetI = a.i(), sommetJ = a.j();
                for (Arete test : temp) {
                    if (!test.equals(a) && (sommetI == test.i() || sommetI == test.j()) && (sommetJ == test.i() || sommetJ == test.j()) && test.i() != test.j()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return ture ssi pour tous sommets i,j de this avec (i!=j), alors this contient une arête {i,j}
     *
     */
    public boolean estComplet() {
        for (Integer i : this.mapAretes.keySet()) {
            HashSet<Arete> h = this.mapAretes.get(i);
            for (Integer j: this.mapAretes.keySet()) {
                if (!Objects.equals(i, j)){ // i != j  => Integer au lieu de int, necessite Objects. pour suppr warning
                    Arete tempArete = new Arete(i, j), tempAreteReverse = new Arete(j, i); // on connait pas l'ordre a l'avance, donc on envisage les 2
                    if (!h.contains(tempArete) && !h.contains(tempAreteReverse)){
                        return false; // l'arrete créée au sens equals du hashSet doit etre la meme pour que ca soit bon
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return true ssi this est une chaîne. Attention, être une chaîne
     * implique en particulier que l'on a une seule arête (et pas plusieurs en parallèle) entre
     * les sommets successifs de la chaîne. On considère que le graphe vide est une chaîne.
     */
    public boolean estUneChaine() {
        int sommetPremierD = -1;
        if (this.nbSommets() == 0 || nbAretes() == 0){
            return true;
        } else if (this.estSimple()) {
            // Premiere partie regarde si les degrés correspondent.
            int nbDegre1 = 0;
            for (Integer i: this.mapAretes.keySet()) {
                int degreDeI = degre(i);
                if (degreDeI == 1){
                    if (nbDegre1 == 2){
                        return false;
                    }
                    else if (nbDegre1 == 1) {
                        for (Arete a : this.mapAretes.get(i)){ // juste pour recuperer l'arete
                            if (a.getAutreSommet(i) == sommetPremierD) // si le seul voisin c'est celui de degré 1
                                return false;
                        }
                    }
                    else if (nbDegre1 == 0)
                        sommetPremierD = i;
                    nbDegre1++;
                } else if (degreDeI > 2) { // 2 ok et 0 aussi mais 3 plus une chaine.
                    return false;
                }
            }
            if (nbDegre1 != 2) // par definition une chaine a une tete et une queue/ un debut et une fin
                return false;
            // Deuxieme partie parcourt sommets pour voir si contient un cycle ou pas (pas besoin de checker si paralleles)
            Queue<Integer> aParcourir = new ArrayDeque<Integer>();// TODO: remplacer par verification de si contient un cycle ou pas en regardant les voisins de chacun a partir du sommetPremierD
            aParcourir.add(sommetPremierD);
            Integer eltPrecedent = null;
            Integer eltActuel = null;
            HashSet<Integer> pointsParcourus = new HashSet<>();

            while(!aParcourir.isEmpty()){
                if (eltActuel != null){// si pas premier sommet a parcourir
                    eltPrecedent = eltActuel;
                    pointsParcourus.add(eltPrecedent);
                }
                eltActuel = aParcourir.poll();
                for (Arete a : this.mapAretes.get(eltActuel)) {
                    Integer sommet2 = a.getAutreSommet(eltActuel);
                    if (pointsParcourus.contains(sommet2)){
                        if (!sommet2.equals(eltPrecedent))
                            return false;
                    } else {
                        aParcourir.add(sommet2);
                    }
                }
            }

        } else
            return false;
        return true;
    }


    /**
     * @return true ssi this est un cycle. Attention, être un cycle implique
     * en particulier que l'on a une seule arête (et pas plusieurs en parallèle) entre
     * les sommets successifs du cycle.
     * On considère que dans le cas où G n'a que 2 sommets {i,j}, et 2 arêtes parallèles {i,j}, alors G n'est PAS un cycle.
     * On considère que le graphe vide est un cycle.
     */
    public boolean estUnCycle() {
           throw new RuntimeException("Méthode non implémentée");
    }


    public boolean estUneForet() {
        throw new RuntimeException("Méthode non implémentée");
    }

    public Set<Integer> getClasseConnexite(int v) {
        throw new RuntimeException("Méthode non implémentée");
    }

    public Set<Set<Integer>> getEnsembleClassesConnexite() {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * @return true si et seulement si l'arête passée en paramètre est un isthme dans le graphe.
     */
    public boolean estUnIsthme(Arete a) {
        throw new RuntimeException("Méthode non implémentée");
    }

    public boolean sontAdjacents(int i, int j) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * Fusionne les deux sommets passés en paramètre.
     * Toutes les arêtes reliant i à j doivent être supprimées (pas de création de boucle).
     * L'entier correspondant au sommet nouvellement créé sera le min{i,j}. Le voisinage du nouveau sommet
     * est l'union des voisinages des deux sommets fusionnés.
     * Si un des sommets n'est pas présent dans le graphe, alors cette fonction ne fait rien.
     */
    public void fusionnerSommets(int i, int j) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre correspond à un graphe simple valide.
     * La pondération des arêtes devrait être ignorée.
     */
    public static boolean sequenceEstGraphe(List<Integer> sequence) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre correspond à un graphe valide.
     * La pondération des arêtes devrait être ignorée.
     */
    public static boolean sontIsomorphes(Graphe g1, Graphe g2) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * Retourne un plus court chemin entre 2 sommets sans répétition de sommets
     * @param depart le sommet de départ
     * @param arrivee le sommet d'arrivée
     * @param pondere true si les arêtes sont pondérées (pas les longueurs des routes correspondantes dans le jeu)
     *                false si toutes les arêtes ont un poids de 1 (utile lorsque les routes associées sont complètement omises)
     */
    public List<Integer> parcoursSansRepetition(int depart, int arrivee, boolean pondere) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * Retourne un chemin entre 2 sommets sans répétition de sommets et sans dépasser
     * le nombre de bateaux et wagons disponibles. Cette fonction supposera que `this` est
     * bien un graphe issu du jeu avec des vraies routes (les objets routes ne sont pas null).
     * Dans cette fonction la couleur des routes n'est pas à prendre en compte.
     *
     * @param depart    le sommet de départ
     * @param arrivee   le sommet d'arrivée
     * @param nbBateaux le nombre de bateaux disponibles
     * @param nbWagons  le nombre de wagons disponibles
     * @return une liste d'entiers correspondant aux sommets du chemin, où l'élément en position 0 de la liste
     * et le sommet de départ, et l'élément à la dernière position de la liste (taille de la liste - 1) est le somme d'arrivée.
     * Si le chemin n'existe pas, retourne une liste vide (initialisée avec 0 éléments).
     * Pré-requis le graphe `this` est un graphe avec des routes (les objets routes ne sont pas null).
     */
    public List<Integer> parcoursSansRepetition(int depart, int arrivee, int nbWagons, int nbBateaux) {
        throw new RuntimeException("Méthode non implémentée");
    }
    /**
     * Retourne un chemin passant une et une seule fois par tous les sommets d'une liste donnée.
     * Les éléments de la liste en paramètres doivent apparaître dans le même ordre dans la liste de sortie.
     *
     * @param listeSommets la liste de sommets à visiter sans répétition ;
     *                     pré-requis : c'est une sous-liste de la liste retournée
     * @return une liste d'entiers correspondant aux sommets du chemin.
     * Si le chemin n'existe pas, retourne une liste vide.
     */
    public List<Integer> parcoursSansRepetition(List<Integer> listeSommets) {
        throw new RuntimeException("Méthode non implémentée");
    }

    /**
     * Retourne un plus petit ensemble bloquant de routes entre deux villes. Cette fonction supposera que `this` est
     * bien un graphe issu du jeu avec des vraies routes (les objets routes ne sont pas null).
     * Dans cette fonction la couleur des routes n'est pas à prendre en compte.
     *
     * @return un ensemble de route.
     * Remarque : l'ensemble retourné doit être le plus petit en nombre de routes (et PAS en somme de leurs longueurs).
     * Remarque : il se peut qu'il y ait plusieurs ensemble de cardinalité minimum.
     * Un seul est à retourner (au choix).
     */
    public Set<Route> ensembleBloquant(int ville1, int ville2) {
        throw new RuntimeException("Méthode non implémentée");
    }
}