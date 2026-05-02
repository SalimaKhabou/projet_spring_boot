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
     * Requête JPQL adaptée à la relation unidirectionnelle ManyToOne :
     * - "c" est un alias pour Classe
     * - Maintenant Classe a un seul utilisateur (c.utilisateur), pas une liste
     * - On filtre sur le niveau passé en paramètre
     * - On compte les utilisateurs distincts (au cas où un utilisateur aurait plusieurs classes)
     *
     * @param nv le niveau à filtrer
     * @return le nombre total d'utilisateurs distincts ayant des classes de ce niveau
     */
    @Query("SELECT COUNT(DISTINCT c.utilisateur) FROM Classe c WHERE c.niveau = :nv AND c.utilisateur IS NOT NULL")
    Integer countUtilisateursByNiveau(@Param("nv") Niveau nv);
}