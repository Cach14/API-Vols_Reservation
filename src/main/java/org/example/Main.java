package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    static final String URL = "jdbc:mysql://localhost:/vol_reservation";
    static final String USER = "root";
    static final String PASSWORD = "mysqlRooT69";

    public static void main(String[] args) {

        Scanner scanner;
        Connection conn;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("connexion établie.");

            scanner = new Scanner(System.in);
            while (true) {

                System.out.println("1.Insérer un nouveau vol");
                System.out.println("2.Insérer un nouveau passager");
                System.out.println("3.Réserver un vol pour un passager");
                System.out.println("4.Consulter les horaires de vol pour une destination spécifique");
                System.out.println("5.Annuler une réservation de vol");
                System.out.println("6.Afficher la liste des réservations pour un passager");
                System.out.println("7.Quitter");

                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1:
                        insererVol(conn, scanner);
                        break;
                    case 2:
                        enregistrerPassager(conn, scanner);
                    case 3:
                        reserverVol(conn, scanner);
                        break;
                    case 4:
                        consulterHorairesVol(conn, scanner);
                        break;
                    case 5:
                        annulerRerservaion(conn, scanner);
                        break;
                    case 6:
                        afficherReservation(conn, scanner);
                        break;
                    case 7:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Choix invalide !");

                }

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    //methodes pour inserer les données

    private static void insererVol(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Ajout d'un nouveau vol.");

        String req = "INSERT INTO vols(compagnie_aerienne, num_vol, date_depart, heure_depart, date_darrivee, heure_darrivee, origine, destination) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        PreparedStatement pstm = conn.prepareStatement(req);

        System.out.print("Nom de la compagnie aérienne: ");
        String compagnie_aerienne = scanner.nextLine();

        System.out.print("Numéro de vol: ");
        String num_vol = scanner.nextLine();

        System.out.print("Date de départ au format AAAA-MM-DD: ");
        String date_depart = scanner.nextLine();

        System.out.print("Heure de départ au format HH:mm:ss: ");
        String heure_depart = scanner.nextLine();

        System.out.print("Date d'arriver au format AAAA-MM-DD: ");
        String date_darrivee = scanner.nextLine();

        System.out.print("Heure de d'arriver au format HH:mm:ss: ");
        String heure_darrivee = scanner.nextLine();

        System.out.print("Origine du vol: ");
        String origine = scanner.nextLine();

        System.out.print("Destination du vol: ");
        String destination = scanner.nextLine();

        pstm.setString(1, compagnie_aerienne);
        pstm.setString(2, num_vol);
        pstm.setDate(3, Date.valueOf(date_depart));
        pstm.setTime(4, Time.valueOf(heure_depart));
        pstm.setDate(5, Date.valueOf(date_darrivee));
        pstm.setTime(6, Time.valueOf(heure_darrivee));
        pstm.setString(7, origine);
        pstm.setString(8, destination);

        pstm.executeUpdate();
        pstm.close();

        System.out.println("*************************** VOL INSERE AVEC SUCCES ***************************");

    }

    private static void enregistrerPassager(Connection conn, Scanner scanner) throws SQLException{
        System.out.println("Enregistrement d'un passager.");

        try {
            String req = "INSERT INTO passagers(nom, prenom, adresse, tel) VALUES (?, ?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(req);

            System.out.print("Nom: ");
            String nom = scanner.nextLine();

            System.out.print("Prenom: ");
            String prenom = scanner.nextLine();

            System.out.print("Adresse: ");
            String adresse = scanner.nextLine();

            System.out.print("Téléphone: ");
            String tel = scanner.nextLine();

            pstm.setString(1, nom);
            pstm.setString(2, prenom);
            pstm.setString(3, adresse);
            pstm.setString(4, tel);

            pstm.executeUpdate();
            pstm.close();

            System.out.println("*************************** PASSAGER ENREGISTRE AVEC SUCCES ***************************");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'insertion en base.");
        }

    }

    private static void reserverVol(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Reservation de vol.");

        String req = "INSERT INTO reservation(vol_id, passager_id, date_reservation) VALUES (?, ?, ?)";
        PreparedStatement pstm = conn.prepareStatement(req);

        System.out.print("ID du vol: ");
        int vol_id = scanner.nextInt();

        System.out.print("ID passager: ");
        int passager_id = scanner.nextInt();

        LocalDate date_reservation = LocalDate.now();

        pstm.setInt(1, vol_id);
        pstm.setInt(2, passager_id);
        pstm.setDate(3, Date.valueOf(date_reservation));

        pstm.executeUpdate();
        pstm.close();

    }
    private static void consulterHorairesVol(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Consultation des horaires de vols pour une destination donnée");

        String req = "SELECT date_depart, heure_depart, date_darrivee, heure_darrivee, origine " +
                "FROM vols WHERE destination = ?;";

        try (PreparedStatement pstm = conn.prepareStatement(req)){

            System.out.print("Entrer la destination: ");
            String destination = scanner.nextLine();

            pstm.setString(1, destination);

            try (ResultSet rs = pstm.executeQuery()){
                if (!rs.isBeforeFirst()){
                    System.out.println("Aucun vol trouvé pour cette destination.");
                }else {
                    System.out.println("Voici les horaires des vols pour la destination: " + destination);
                    System.out.println("##########################################");
                    while (rs.next()) {
                        System.out.println("Date de départ: " + rs.getDate(1));
                        System.out.println("Heure de départ: " + rs.getTime(2));
                        System.out.println("Date de d'arriver: " + rs.getDate(3));
                        System.out.println("Heure de départ: " + rs.getTime(4));
                        System.out.println("Origine: " + rs.getString(5));
                        System.out.println("##########################################");
                    }
                }pstm.close();
            }
        }catch (SQLException e) {
            System.out.println("Une erreur est survenue lors de la consultation des horaires.");
            System.out.println(e.getMessage());
        }

    }
    private static void annulerRerservaion(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Annulation de reservation");

        String req = "UPDATE reservation SET statut='annulée' WHERE id = ?";
        PreparedStatement pstm = conn.prepareStatement(req);

        System.out.println("ID de la reservation: ");
        int id = scanner.nextInt();

        pstm.setInt(1, id);
        pstm.executeUpdate();

        System.out.println("*************************** ANNULATION REUSSIE ***************************");
        pstm.close();

    }
    private static void afficherReservation(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Liste de réservations");
        //A FAIRE LE MATIN: RECUPERER AUSSI LES INFORMATIONS SUR LE VOL
        String req = "SELECT r.id, r.vol_id, p.nom, p.prenom " +
                "FROM reservation r " +
                "JOIN passagers p ON r.passager_id = p.id " +
                "WHERE passager_id = ?";

        try (PreparedStatement pstm = conn.prepareStatement(req)){
            System.out.print("ID passager: ");
            String id = scanner.nextLine();

            pstm.setString(1, id);

            try (ResultSet rs = pstm.executeQuery()){
                if (!rs.isBeforeFirst()){
                    System.out.println("Aucune reservayion trouvée pour ce passager.");
                }else {

                    while (rs.next()){
                        System.out.println("Voici les reservations trouvé pour le passager: " + id);
                        System.out.println("##########################################");
                        while (rs.next()) {
                            System.out.println("ID reservation: " + rs.getInt(1));
                            System.out.println("ID vol: " + rs.getInt(2));
                            System.out.println("Nom: " + rs.getString("nom"));
                            System.out.println("Prenom: " + rs.getString("prenom"));
                            System.out.println("##########################################");

                        }

                    }

                }

            }
        }

    }



}