package tn.fst.projet_jee.repositories;

import tn.fst.projet_jee.entities.Classe;
import tn.fst.projet_jee.entities.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'entité Classe.
 * Hérite de JpaRepository<Classe, Integer> :
 *   save(), findById(), findAll(), deleteById(), count()...
 */
@Repository
public interface ClasseRepository extends JpaRepository<Classe, Integer> {

    /**
     * Retrouve une Classe par son titre (convention de nommage Spring Data).
     * Spring génère automatiquement : SELECT * FROM classe WHERE titre = ?
     *
     * @param titre ex: "4AG1"
     * @return la Classe correspondante, ou null si inexistante
     */
    Classe findByTitre(String titre);

    /**
     * Compte le nombre d'utilisateurs dans les classes d'un niveau donné.
     *
     * ══ JPQL adapté à la relation UNIDIRECTIONNELLE ══════════════════════
     *
     * Requête correcte — on part de UTILISATEUR (qui connaît sa Classe) :
     *    SELECT COUNT(u) FROM Utilisateur u
     *    WHERE u.classe IS NOT NULL AND u.classe.niveau = :nv
     *
     *    Explication pas à pas :
     *    1. On itère sur tous les Utilisateurs
     *    2. u.classe → Hibernate fait le JOIN automatique grâce au @ManyToOne
     *       SQL généré : INNER JOIN classe c ON u.code_classe = c.code_classe
     *    3. u.classe IS NOT NULL → exclut les utilisateurs non encore affectés
     *    4. u.classe.niveau = :nv → filtre sur le niveau de la Classe
     *    5. COUNT(u) → compte les utilisateurs correspondants
     *
     *    SQL équivalent :
     *      SELECT COUNT(u.id_utilisateur)
     *      FROM utilisateur u
     *      INNER JOIN classe c ON u.code_classe = c.code_classe
     *      WHERE u.code_classe IS NOT NULL
     *        AND c.niveau = 'QUATRIEME'
     *
     * Note : cette JPQL est valide dans ClasseRepository même si elle
     *         référence l'entité Utilisateur, car JPQL opère sur les
     *         entités (pas sur les tables), sans restriction de repository.
     *
     * Test question e) :
     *   Après affectation Amna → 4AG1 (QUATRIEME), Ahmed → 5EM1 (CINQUIEME) :
     *   countUtilisateursByNiveau(QUATRIEME) → 1 ✓
     *   countUtilisateursByNiveau(CINQUIEME) → 1 ✓
     * ─────────────────────────────────────────────────────────────────────
     *
     * @param nv le niveau à filtrer (ex: Niveau.QUATRIEME)
     * @return le nombre d'utilisateurs dans les classes de ce niveau
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.classe IS NOT NULL AND u.classe.niveau = :nv")
    Integer countUtilisateursByNiveau(@Param("nv") Niveau nv);
}