package tn.fst.projet_jee.repositories;


import tn.fst.projet_jee.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'entité Utilisateur.
 *
 * JpaRepository fournit automatiquement les méthodes CRUD de base :
 * save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * @param Utilisateur : type de l'entité gérée
 * @param Integer     : type de la clé primaire (idUtilisateur)
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    // Aucune méthode personnalisée nécessaire pour ce TP
    // Spring Data JPA génère toutes les requêtes de base automatiquement
}