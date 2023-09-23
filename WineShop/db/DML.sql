USE WineShop;
DELETE FROM customer;
DELETE FROM employee;
DELETE FROM courier;
DELETE FROM supplier;
DELETE FROM wine;
DELETE FROM itemLedgerEntry;


INSERT INTO customer (name, surname, cf, email, phone, address, username, password) VALUES
("Mirko", "Piazza", "PZZMRK00S01C351J", "mirko.piazza@studenti.unipr.it", "3394880000", "via Mirko, 1", "mpiazza", "password"),
("Camilla", "Franceschini", "FRNCML00S02C351J", "camilla.franceschini1@studenti.unipr.it", "3394880001", "via Camilla, 2", "cfranceschini", "password"),
("Nicola", "Tesla", "TSLNCL00S03C351J", "nicola.tesla@studenti.unipr.it", "3394880002", "via Nicola, 3", "ntesla", "password"),
("Alfonso", "Battiato", "BTTLFN00S04C351J", "alfonso.battiato@studenti.unipr.it", "3394880003", "via Alfonso, 4", "abattiato", "password");

INSERT INTO employee (name, surname, cf, email, phone, address, username, password, admin) VALUES
("Mirko", "Costanzo", "CSTMRK00S01C351J", "mirko.costanzo@studenti.unipr.it", "3394880004", "via Mirko, 2", "mcostanzo", "password", 1),
("Camilla", "Camillini", "CMLCML00S02C351J", "camilla.camillini1@studenti.unipr.it", "3394880005", "via Camilla, 3", "ccamillini", "password", 0),
("Nicola", "Mazda", "MZDNCL00S03C351J", "nicola.mazda@studenti.unipr.it", "3394880006", "via Nicola, 4", "nmazda", "password", 0),
("Alfonso", "Celentano", "CLNLFN00S04C351J", "alfonso.celentano@studenti.unipr.it", "3394880007", "via Alfonso, 5", "acelentano", "password", 0);

INSERT INTO courier (name, email, address, phone, piva) VALUES
("TNT", "info@tnt.it", "Via tnt, 1", "0521123400", 01234567891),
("GLS", "info@gls.it", "Via gls, 1", "0521123401", 01234567892),
("Bartolini", "info@bartolini.it", "Via bartolini, 1", "0521123402", 01234567893);

INSERT INTO supplier (name, email, address, phone, piva) VALUES
("Vini rossi srl", "info@vinirossi.it", "Via rossi, 1", "0521123403", 01234567894),
("Vini bianchi srl", "info@vinibianchi.it", "Via bianchi, 1", "0521123404", 01234567895);

INSERT INTO wine (name, producer, origin, year, technical_notes, vines, sale_price, purchase_price, threshold, id_courier, id_supplier) VALUES
("Sassicaia", "Tenuta San Guido", "Toscana", 2017, "Sassicaia è un vino rosso Bolgheri DOC e ha una gradazione alcolica del 14%", "CABERNET SAUVIGNON", 250, 200, 6, 1, 1),
("Tignanello", "Marchesi Antinori", "Toscana", 2018, "Tignanello è un vino rosso Toscana IGT e ha una gradazione alcolica del 13,5%", "SANGIOVESE", 100, 75, 6, 1, 1),
("Ornellaia", "Ornellaia", "Toscana", 2012, "Ornellaia è un vino rosso Bolgheri DOC e ha una gradazione alcolica del 14%", "CABERNET SAUVIGNON", 300, 225, 6, 2, 1),
("Solaia", "Marchesi Antinori", "Toscana", 2016, "Solaia è un vino rosso Toscana IGT e ha una gradazione alcolica del 14%", "CABERNET SAUVIGNON", 300, 220, 6, 2, 1),
("Il Greppo", "Biondi Santi", "Toscana", 2013, "Il Greppo è un vino rosso Brunello di Montalcino DOCG e ha una gradazione alcolica del 13%", "SANGIOVESE", 180, 150, 6, 3, 1),
("Angialis", "Argiolas", "Sardegna", 2014, "Angialis è un vino bianco Isola dei Nuraghi IGT e ha una gradazione alcolica del 14,5%", "NASCO", 50, 35, 6, 3, 2),
("Vittorio Moretti", "Bellavista", "Lombardia", 2004, "Vittorio Moretti è un vino bianco Franciacorta DOCG e ha una gradazione alcolica del 12,5%", "CHARDONNAY", 150, 120, 6, 2, 2),
("Brut", "Barone Pizzini", "Lombardia", 2016, "Brut è un vino bianco Franciacorta DOCG e ha una gradazione alcolica del 12%", "CHARDONNAY", 300, 220, 6, 1, 2),
("Kabir", "Donnafugata", "Sicilia", 2016, "Kabir è un vino bianco Moscato di Pantelleria DOC e ha una gradazione alcolica del 12%", "MOSCATO DI ALESSANDRIA", 30, 20, 6, 2, 2),
("Khamma", "Salvatore Murana", "Sicilia", 2016, "Khamma è un vino bianco Passito di Pantelleria DOC e ha una gradazione alcolica del 15,5%", "MOSCATO DI ALESSANDRIA", 40, 30, 6, 3, 2);

INSERT INTO itemLedgerEntry (id_order, quantity, price, type, state, order_date, assignation_date, proposal_delivery, delivery_date, review_employee, id_wine, id_employee, id_customer) VALUES
(1, 10, 2000, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 1, 2, null),
(2, 30, 22500, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 2, 2, null),
(3, 30, 6750, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 3, 2, null),
(4, 30, 6600, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 4, 2, null),
(5, 30, 4500, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 5, 2, null),
(6, 30, 1050, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 6, 2, null),
(7, 30, 3600, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 7, 2, null),
(8, 30, 6600, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 8, 2, null),
(9, 30, 600, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 9, 2, null),
(10, 30, 900, "Acquisto", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, "2023-04-16 11:30:00", null, 10, 2, null),
(11, 1, 250, "Vendita", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", 3, "2023-04-18 11:30:00", 5, 1, 2, 1),
(12, 1, 250, "Vendita", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", 3, "2023-04-18 11:30:00", 3, 1, 3, 1),
(13, 1, 250, "Vendita", "Completato", "2023-04-15 10:00:00", "2023-04-15 10:00:00", 3, "2023-04-18 11:30:00", 4, 1, 2, 1),
(14, 1, 250, "Vendita", "Richiesto", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, null, null, 1, 4, 1),
(15, 1, 100, "Vendita", "Richiesto", "2023-04-15 10:00:00", "2023-04-15 10:00:00", null, null, null, 2, 3, 1),
(16, 1, 250, "Vendita", "ConfermaUtente", "2023-04-29 10:00:00", "2023-04-29 10:00:00", 5, null, null, 1, 2, 1),
(17, 1, 250, "Vendita", "Richiesto", "2023-05-01 10:00:00", "2023-05-01 10:00:00", null, null, null, 1, 2, 1);