## 1- 7071 de ayağa kalkacak bir "user-service" mikroservisi oluşturalım.
## Ve iki servisimiz içinde postgres bağlantılarını gerçekleştirelim.(java13AuthDB, java13UserDB)
## ÖDEV SORUSU: AUTH' da gerekli katmanları ekleyelim sonrasında bir register metodu yazalım. Register metodu
## dto alsın ve geriye dto dönsün bunun için gerekli mapper işlemlerinide gerçekleştirelim.

## 2- Disaridan login olmak için gerekli parametreleri alalımç eger bilgiler dogru ise bize true yanlis ise false donsun.

## 3- Validasyon islemlerini yapalim. Aklimiza gelen basit validasyonlari kullanalim.

## 4- Kullanıcının Statusunu aktif hale getirmek için aktivayson kod doğrulaması yapan bir metot yazınız.

## 5- Auth da status u aktif hale getirdiğimde user servicedeki statusta aktif hale gelsin.

## 6- Login metodunu revize edelim bize token dönsün statüsü aktif olmayan kullanıcılar giriş yapamasın.

## 7- User'da email'imi değişirken auth'da da değişsin istiyorum. Bunun için user-service'den -> auth-service'e bir feign client bağlantısı gerçekleştirelim.