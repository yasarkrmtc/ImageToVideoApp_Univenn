ImageToVideo Uygulaması

ImageToVideo, AI tabanlı bir uygulamadır. Bu uygulama, kullanıcıların görüntüleri video formatına dönüştürmesine olanak tanır. Modern Android geliştirme teknikleri kullanılarak geliştirilmiş bu uygulama, sürdürülebilirlik ve modülerlik adına MVVM (Model-View-ViewModel) mimarisiyle tasarlanmıştır. Uygulamanın temel amacı, verilerin sunumunu ve iş mantığını ayırarak test edilebilirliği ve yönetilebilirliği artırmaktır.

Temel Özellikler
MVVM Mimarisi: Model-View-ViewModel prensibiyle geliştirilmiştir. Bu yaklaşım, iş mantığını görsel katmandan ayırarak kodun daha temiz ve sürdürülebilir olmasını sağlar.

Apollo Server Kullanımı: API çağrıları için Apollo Client kullanılmıştır. Apollo Client, GraphQL API'lerinden verileri alırken daha esnek ve güçlü bir veri yönetimi sağlar.

Bağımlılık Yönetimi: Hilt kullanılarak bağımlılık enjeksiyonu yapılmış, böylece uygulamanın bağımlılıkları daha kolay yönetilebilir olmuştur. Bu sayede modüler yapı daha verimli şekilde uygulanmıştır.

Asenkron Veri Yönetimi: Uygulama, asenkron veri yönetimi için Kotlin Flow kullanır, bu da veri akışlarının daha verimli ve reaktif bir şekilde yönetilmesini sağlar.

Görsel Yükleme: Glide kütüphanesi kullanılarak görsellerin hızlı ve verimli bir şekilde yüklenmesi sağlanmıştır.

ClickWithDebounce: Bu fonksiyon, kullanıcı etkileşimlerinde hızlı ve tekrar eden tıklamaları engelleyerek daha akıcı bir deneyim sunar.

Kullanılan Teknolojiler
Apollo Client (API Yönetimi)

Dagger-Hilt (Bağımlılık Enjeksiyonu)

Kotlin Flow (Asenkron Veri Yönetimi)

Coin (Görsel Yükleme)

MVVM Mimarisi

Kotlin (Ana Dil)

Nasıl Çalışır?
Uygulama, kullanıcının yüklediği görselleri alır, işleme yaparak videoya dönüştürür. Bu süreçte, görsel işleme işlemleri arka planda gerçekleşirken, kullanıcıya gerçek zamanlı bir deneyim sunulur. API verisi, Apollo Client aracılığıyla bir GraphQL sunucusundan alınır ve sonuçlar hızlı bir şekilde kullanıcıya sunulur.

API Kullanımı
API çağrıları, Apollo Server üzerinden gerçekleştirilir. Apollo Client, GraphQL sorguları ile uygulamaya veri sağlar. API'den alınan veriler, Kotlin Flow kullanılarak asenkron bir şekilde yönetilir ve UI'ya sunulur.


<img src="https://github.com/user-attachments/assets/17d787ff-56e1-431d-aae5-8888a4d13f7f" width="250" />
<img src="https://github.com/user-attachments/assets/b5886764-26d8-47ee-95d2-bf58baf23bd8" width="250" />
<img src="https://github.com/user-attachments/assets/cd306015-1460-4f1c-933c-09e05542733c" width="250" />
<img src="https://github.com/user-attachments/assets/fe2c6a33-49fe-4358-97b2-4150d9eee5bf" width="250" />

