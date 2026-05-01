package tn.fst.projet_jee.repositories;


import tn.fst.projet_jee.entities.Classe;
import tn.fst.projet_jee.entities.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'entité Classe.
 *
 * Contient des méthodes de requête personnalisées en JPQL
 * pour répondre aux besoins spécifiques du TP.
 */
@Repository
public interface ClasseRepository extends JpaRepository<Classe, Integer> {

    /**
     * Retrouve une classe par son titre (ex: "4AG1").
     * Utilise la convention de nommage Spring Data : findBy + NomDuChamp.
     *
     * @param titre le titre de la classe recherchée
     * @return la Classe correspondante ou null si inexistante
     */
    Classe findByTitre(String titre);

    /**
     * Compte le nombre d'utilisateurs (étudiants) dans les classes d'un niveau donné.
     *
     * Requête JPQL :
     * - "c" est un alias pour Classe
     * - "u" est un alias pour les Utilisateur dans la liste c.utilisateurs
     * - On filtre sur le niveau passé en paramètre
     * - On compte les utilisateurs distincts (pour éviter les doublons si un user est dans plusieurs classes du même niveau)
     *
     * @param nv le niveau à filtrer
     * @return le nombre total d'utilisateurs dans les classes de ce niveau
     */
    @Query("SELECT COUNT(DISTINCT u) FROM Classe c JOIN c.utilisateurs u WHERE c.niveau = :nv")
    Integer countUtilisateursByNiveau(@Param("nv") Niveau nv);
}