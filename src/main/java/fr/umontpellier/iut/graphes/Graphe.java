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
            this.mapAretes.put(i, new HashSet<>());
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
                    if(!this.getVoisins(i).contains(j))
                        return false;
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
        int sommetDegre1 = 0;
        for (Integer x : this.mapAretes.keySet()) {
            if (!this.getClasseConnexite(x).equals(this.mapAretes.keySet())){
                return false;
            }
            if (degre(x) == 1){
                sommetDegre1++;
                if (sommetDegre1 == 3)
                    return false;
            } else if (degre(x) != 2) {
                return false;
            }
        }
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
        for (Integer x : this.mapAretes.keySet()) {
            if (!this.getClasseConnexite(x).equals(this.mapAretes.keySet())) {
                return false;
            }
            else if (degre(x) != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean estUnArbre(){
        if (this.nbSommets() == 0 || nbAretes() == 0 && nbSommets() > 1)
            return false;
        else if (this.nbSommets() == 1)
            return true;
        Queue<Integer> vert = new ArrayDeque<>();
        for (Integer i :this.mapAretes.keySet()) {
            vert.add(i);
            break;
        }
        Queue<Integer> rouge = new ArrayDeque<>();
        while (!vert.isEmpty()){
            int x = vert.remove();
            for (Integer y: this.getVoisins(x)) {
                if (vert.contains(y))
                    return false; // si deja dans ceux a explorer ensuite alors ca boucle
                if (!vert.contains(y) && !rouge.contains(y))
                    vert.add(y);
            }
            rouge.add(x);
        }
        HashSet<Integer> bleu = new HashSet<>(vert); // normalement vert est vide donc pas obligé mais au cas ou
        bleu.addAll(rouge);
        return bleu.equals(this.mapAretes.keySet());
    }

    public boolean estUneForet() {
        if (nbSommets() == 0 || nbSommets() == 1 || nbSommets() > 1 && nbAretes() == 0){
            return true;
        }
        for (Set<Integer> classeDeC : this.getEnsembleClassesConnexite()){ // si chaque composante connexe = arbre alors foret
            Graphe sousGraphe = new Graphe(this, classeDeC);
            if(!sousGraphe.estUnArbre()){
                return false;
            }
        }
        return true;
    }

    public Set<Integer> getClasseConnexite(int v) {
        Queue<Integer> vert = new ArrayDeque<>();
        vert.add(v);
        Queue<Integer> rouge = new ArrayDeque<>();
        while (!vert.isEmpty()){
            int x = vert.remove();
            for (Integer y: this.getVoisins(x)) {
                if (!vert.contains(y) && !rouge.contains(y))
                    vert.add(y);
            }
            rouge.add(x);
        }
        HashSet<Integer> bleu = new HashSet<>(vert); // normalement vert est vide donc pas obligé mais au cas ou
        bleu.addAll(rouge);
        return bleu;
    }

    public Set<Set<Integer>> getEnsembleClassesConnexite() {
        Set<Set<Integer>> classes = new HashSet<>();
        for (Integer x :this.mapAretes.keySet()) {
            classes.add(getClasseConnexite(x));
        }
        return classes;
    }

    /**
     * @return true si et seulement si l'arête passée en paramètre est un isthme dans le graphe.
     */
    public boolean estUnIsthme(Arete a) {
        if (!existeArete(a)){
            return false;
        }
        Graphe copieGraphe = new Graphe(this, this.mapAretes.keySet());
        int nbClassesdeConnexite = copieGraphe.getEnsembleClassesConnexite().size();
        copieGraphe.supprimerArete(a);
        return nbClassesdeConnexite < copieGraphe.getEnsembleClassesConnexite().size();// inferieur = nouvelle ClcG
    }

    public boolean sontAdjacents(int i, int j) {
        return getVoisins(i).contains(j);
    }

    /**
     * Fusionne les deux sommets passés en paramètre.
     * Toutes les arêtes reliant i à j doivent être supprimées (pas de création de boucle).
     * L'entier correspondant au sommet nouvellement créé sera le min{i,j}. Le voisinage du nouveau sommet
     * est l'union des voisinages des deux sommets fusionnés.
     * Si un des sommets n'est pas présent dans le graphe, alors cette fonction ne fait rien.
     */
    public void fusionnerSommets(int i, int j) {
        if (!this.mapAretes.containsKey(i) || !this.mapAretes.containsKey(j))
            return;
        int nouveauSommet = Math.min(i, j);
        // supprimer aretes de i a j
        HashSet<Arete> aretesI = this.mapAretes.get(i);
        HashSet<Arete> aretesJ = this.mapAretes.get(j);
        for (Arete aDeI: aretesI)
            if (aDeI.getAutreSommet(i) == j)
                supprimerArete(aDeI);
        for (Arete aDeJ: aretesJ)    // deuxieme partie pas obligée car automatique mais on sait jamais
            if (aDeJ.getAutreSommet(j) == i)
                supprimerArete(aDeJ);
        // fusion aretes en un point
        if (i == nouveauSommet){
            for (Arete aDeJ: this.mapAretes.get(j)) {
                ajouterArete(new Arete(nouveauSommet, aDeJ.getAutreSommet(j)));
            }
            supprimerSommet(j);
        }
        else {
            for (Arete aDeI: this.mapAretes.get(i)) {
                ajouterArete(new Arete(nouveauSommet, aDeI.getAutreSommet(i)));
            }
            supprimerSommet(i);
        }

    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre correspond à un graphe simple valide.
     * La pondération des arêtes devrait être ignorée.
     */
    public static boolean sequenceEstGraphe(List<Integer> sequence) {
        int n = sequence.size();
        Graphe graphe = new Graphe(n);

        for (int sommet : sequence) {
            if (sommet < 0 || sommet >= n) {
                return false;
            }
        }

        // Construire les aretes du graphe
        for (int i = 0; i < n - 1; i++) {
            int sommet1 = sequence.get(i);
            int sommet2 = sequence.get(i + 1);
            Arete arete = new Arete(sommet1, sommet2);

            // Si graphe simple alors pas de doublons d'arete
            if (graphe.existeArete(arete)) {
                return false;
            }
            graphe.ajouterArete(arete);
        }
        return graphe.estSimple();
    }

    // crée une liste des degrés des sommets, les trie dans l'ordre décroissant, puis renvoie cette liste.
    public static List<Integer> sequenceDegres(Graphe g) {
        List<Integer> sequence = new ArrayList<>();

        for (int sommet : g.ensembleSommets()) {
            int degree = g.degre(sommet);
            sequence.add(degree);
        }

        sequence.sort(Comparator.reverseOrder(  ));
        return sequence;
    }


    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre correspond à un graphe valide.
     * La pondération des arêtes devrait être ignorée.
     */
    public static boolean sontIsomorphes(Graphe g1, Graphe g2) {
        if (g1.nbSommets() != g2.nbSommets() || g1.nbAretes() != g2.nbAretes()) {
            return false;
        }

        List<Integer> sequence1 = sequenceDegres(g1);
        List<Integer> sequence2 = sequenceDegres(g2);

        return sequence1.equals(sequence2);
    }







    /**
     * Retourne un plus court chemin entre 2 sommets sans répétition de sommets
     * @param depart le sommet de départ
     * @param arrivee le sommet d'arrivée
     * @param pondere true si les arêtes sont pondérées (pas les longueurs des routes correspondantes dans le jeu)
     *                false si toutes les arêtes ont un poids de 1 (utile lorsque les routes associées sont complètement omises)
     */


    public List<Integer> parcoursSansRepetition(int depart, int arrivee, boolean pondere) {
        if (pondere){
            HashMap<Integer, Couple> parcours = new HashMap<>();
            for (Integer sommet:this.mapAretes.keySet()) {
                if (sommet.equals(depart)){
                    parcours.put(sommet, new Couple(0));
                }
                else
                    parcours.put(sommet, new Couple());
            }
            PriorityQueue<Integer> file = new PriorityQueue<>(new Comparator<Integer>() { // on crée la file qui implementera dijkstra
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (parcours.get(o1).getDistance() < parcours.get(o2).getDistance()){
                        return o1;
                    }
                    return o2;
                }
            });
            file.add(depart);
            while (!file.isEmpty()){
                Integer sommetActuel = file.poll();
                for (Integer i: this.getVoisins(sommetActuel)) {
                    Couple elt = parcours.get(i);
                    Arete areteAParcourir = null;
                    for (Arete a : this.mapAretes.get(i))
                        if (a.getAutreSommet(i) == sommetActuel) {
                            areteAParcourir = a;
                        }
                    if (areteAParcourir.route() != null)
                        if (parcours.get(sommetActuel).getDistance() + areteAParcourir.route().getLongueur() < elt.getDistance()){
                            elt.setPrevious(sommetActuel);
                            elt.setDistance(parcours.get(sommetActuel).getDistance() + areteAParcourir.route().getLongueur());
                            file.add(i);
                        }
                }
            }
            Couple cplActuel = parcours.get(arrivee);
            List<Integer> cheminLePlusCourt = new ArrayList<>();
            cheminLePlusCourt.add(0, arrivee);
            while (cplActuel.getPrevious() != null){
                cheminLePlusCourt.add(0, cplActuel.getPrevious());
                cplActuel = parcours.get(cplActuel.getPrevious());
            }
            return  cheminLePlusCourt;
        } else{
            List<Integer> parcours = new ArrayList<>();
            Set<Integer> visite = new HashSet<>();
            Queue<Integer> file = new LinkedList<>();

            // Ajouter le sommet de départ à la file et à l'ensemble de visite
            file.add(depart);
            visite.add(depart);

            // Parcours en largeur
            while (!file.isEmpty()) {
                int sommetActuel = file.poll();
                if (sommetActuel == arrivee) {
                    break;
                }
                for (Arete arete : this.mapAretes.get(sommetActuel)) {
                    int sommetSuivant = arete.getAutreSommet(sommetActuel);

                    if (!visite.contains(sommetSuivant)) {
                        visite.add(sommetSuivant);
                        file.add(sommetSuivant);
                    }
                }
            }

            // parcours à partir du sommet d'arrivée en remontant les prédécesseurs
            int sommetCourant = arrivee;
            parcours.add(sommetCourant);

            while (sommetCourant != depart) {
                for (Arete arete : this.mapAretes.get(sommetCourant)) {
                    int sommetPrecedent = arete.getAutreSommet(sommetCourant);
                    if (visite.contains(sommetPrecedent)) {
                        parcours.add(sommetPrecedent);
                        sommetCourant = sommetPrecedent;
                        break;
                    }
                }
            }

            Collections.reverse(parcours); // Inverser l'ordre des sommets pour obtenir le parcours dans le bon sens
            return parcours;
        }
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
     * est le sommet de départ, et l'élément à la dernière position de la liste (taille de la liste - 1) est le sommet d'arrivée.
     * Si le chemin n'existe pas, retourne une liste vide (initialisée avec 0 éléments).
     * Pré-requis le graphe `this` est un graphe avec des routes (les objets routes ne sont pas null).
     */
    public List<Integer> parcoursSansRepetition(int depart, int arrivee, int nbWagons, int nbBateaux) {
        List<Integer> parcours = new ArrayList<>();
        Set<Integer> visite = new HashSet<>();
        Queue<Integer> file = new LinkedList<>();

        file.add(depart);
        visite.add(depart);

        while (!file.isEmpty()) {
            int sommetActuel = file.poll();
            if (sommetActuel == arrivee) {
                break;
            }
            for (Arete arete : this.mapAretes.get(sommetActuel)) {
                int sommetSuivant = arete.getAutreSommet(sommetActuel);
                if (!visite.contains(sommetSuivant) && nbWagons >= arete.route().getLongueur() && nbBateaux >= arete.route().getLongueur()) {
                    visite.add(sommetSuivant);
                    file.add(sommetSuivant);
                }
            }
        }

        // parcours à partir du sommet d'arrivée en remontant les prédécesseurs
        int sommetCourant = arrivee;
        parcours.add(sommetCourant);

        while (sommetCourant != depart) {
            for (Arete arete : this.mapAretes.get(sommetCourant)) {
                int sommetPrecedent = arete.getAutreSommet(sommetCourant);
                if (visite.contains(sommetPrecedent)) {
                    parcours.add(sommetPrecedent);
                    sommetCourant = sommetPrecedent;
                    break;
                }
            }
        }

        Collections.reverse(parcours); // Inverser l'ordre pour obtenir le parcours dans le bon sens
        return parcours;
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
        List<Integer> parcours = new ArrayList<>();
        Set<Integer> visite = new HashSet<>();
        Queue<Integer> file = new LinkedList<>();

        // Vérifier si la liste des sommets à visiter est valide
        if (!listeEstOrdoner(listeSommets)) {
            return parcours; // Retourner une liste vide si la liste des sommets à visiter n'est pas une sous-liste valide
        }

        // Ajouter le premier sommet de la liste à la file et à l'ensemble de visite
        int premierSommet = listeSommets.get(0);
        file.add(premierSommet);
        visite.add(premierSommet);

        // Parcours en largeur
        while (!file.isEmpty()) {
            int sommetActuel = file.poll();
            if (sommetActuel == listeSommets.get(listeSommets.size() - 1)) {
                break;
            }
            for (Arete arete : this.mapAretes.get(sommetActuel)) {
                int sommetSuivant = arete.getAutreSommet(sommetActuel);
                if (!visite.contains(sommetSuivant)) {
                    visite.add(sommetSuivant);
                    file.add(sommetSuivant);
                }
            }
        }

        // Parcours à partir du dernier sommet de la liste en remontant les prédécesseurs
        int sommetCourant = listeSommets.get(listeSommets.size() - 1);
        parcours.add(sommetCourant);

        for (int i = listeSommets.size() - 2; i >= 0; i--) {
            int sommetPrecedent = listeSommets.get(i);
            if (visite.contains(sommetPrecedent)) {
                parcours.add(sommetPrecedent);
                sommetCourant = sommetPrecedent;
            } else {
                return new ArrayList<>(); // Retourner une liste vide si le chemin n'existe pas
            }
        }

        Collections.reverse(parcours); // Inverser l'ordre des sommets pour obtenir le parcours dans le bon sens
        return parcours;
    }

    /**
     * Vérifie si la liste de sommets donnée est une sous-liste valide du graphe.
     * Une sous-liste valide signifie que les sommets apparaissent dans le même ordre dans le graphe.
     *
     * @param listeSommets la liste de sommets à vérifier
     * @return true si la liste de sommets est une sous-liste valide, false sinon
     */
    private boolean listeEstOrdoner(List<Integer> listeSommets) {
        List<Integer> parcours= parcoursSansRepetition(0, this.mapAretes.size() - 1, false);

        int i = 0;
        for (int sommet : parcours) {
            if (sommet == listeSommets.get(i)) {
                i++;
                if (i == listeSommets.size()) {
                    return true;
                }
            }
        }

        return false;
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

    public Map<Integer, HashSet<Arete>> getMapAretes() {
        return mapAretes;
    }

}