# ETL
Projekt zaliczeniowy - Hurtownie Danych
ETL to aplikacja stworzona jako projekt zaliczeniowy z przedmiotu Hurtownie Danych na kierunku Informatyka Stosowana na Uniwersytecie Ekonomicznym w Krakowie. Głównym celem aplikacji jest przeprowadzenie procesu ETL (Extract, Trasform, Load) na danych pobranych z serwisu Ceneo.pl.
###[Pobierz aplikację](https://play.google.com/store/apps/details?id=pl.krakow.uek&hl=pl)

## Wykorzystane technologie
- aplikacja została napisana na system operacyjny Android - minimalna wersja systemu 4.4
- do przetwarzania plików html z danymi o produktach wykorzystana została biblioteka [jsoup](https://jsoup.org/) w wersji 1.9.2  
- do przechowywania danych lokalnie wykorzystane zostało rozwiązanie [realm.io](https://realm.io/docs/java/latest/) w wersji 2.2.0 dla języka Java

## Dokumentacja techniczna
[Link do dokumentacji](http://v-ie.uek.krakow.pl/~s181182/)

## Projekt bazy danych
Ponieważ technologia użyta do przechowywania danych jest obiektowa istnieją dwie klasy reprezentujące obiekty w bazie danych:
- [Product](http://v-ie.uek.krakow.pl/~s181182/pl/krakow/uek/model/Product.html) - klasa reprezentująca produkty
- [Review](http://v-ie.uek.krakow.pl/~s181182/pl/krakow/uek/model/Review.html) - klasa reprezentująca opinie o produktach
Każdy produkt posiada listę z obiektami typu Review - co odpowiada relacji 1 - &#8734; w relacyjnych bazach danych 

## Instrukcja użytkownika
[Link do instrukcji](https://docs.google.com/presentation/d/1HMFL0xjRb8wYqnbS5GlmsSTnOxsO483PdHdGJ8sx2bM/edit#slide=id.p)
