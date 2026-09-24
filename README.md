# FilmKaynak — CloudStream başlangıç deposu

**Durum:** Kaynak kodu hazır bir beta iskeletidir. `hdfilmcehennemi.nl` ana sayfası dışarıdan 403 döndürdüğü için canlı DOM/oynatma akışı doğrulanamadı. `.cs3` derlemesi bu ortamda yapılmadı. Bu proje kurulu/çalışan kaynak iddiası taşımaz.

## Mimari
- Kotlin CloudStream eklentisi: `FilmPlugin` + `FilmProvider`.
- Katalog: genel HTML kart seçicileri, `/page/N/` sayfalama varsayımı.
- Arama: WordPress tipi `?s=` varsayımı.
- Detay: `h1`, OpenGraph görseli ve meta açıklaması.
- Oynatma: yalnızca HTML5 `<video>` / `<source>` ve doğrudan HTTPS `.mp4`/`.m3u8` bağlantıları. Üçüncü taraf gömülü oynatıcı, DRM, captcha veya erişim kontrolü aşma yok.

## Gerekenler
1. GitHub'da `cloudstream-film-baslangic` isimli **public** repo oluşturun.
2. `repo.json` ve kök `build.gradle.kts` içindeki `YOUR_USER` yer tutucularını kendi GitHub kullanıcı adınızla değiştirin. Kök dosyada `setRepo(...)` satırı GitHub Actions'ta otomatik `GITHUB_REPOSITORY` kullanır.
3. Bu klasördeki bütün dosyaları `main` dalına yükleyin.
4. GitHub > Settings > Actions > General > Workflow permissions: Read and write permissions seçin. Workflow'u çalıştırın.
5. İşlem başarılı olduğunda `https://raw.githubusercontent.com/KULLANICI/cloudstream-film-baslangic/builds/repo.json` adresini CloudStream > Ayarlar > Eklentiler > Depo ekle alanına yazın.
6. Eklentiyi yükleyip arama, kart, detay ve oynatma testlerini ayrı ayrı yapın.

**Önemli:** GitHub Actions derlemesi `builds` dalını force-push ile yeniler; bu dalı elle düzenlemeyin. Derleme hatasında Actions loguna bakın. Wrapper JAR ve scriptleri kasıtlı olarak paketlenmedi; GitHub Actions sistem Gradle 8.12 kullanır.

## Canlı site test kontrol listesi
- [ ] Ana sayfa 200 döndürüyor mu? 403 ise dur; korumayı aşmaya çalışma.
- [ ] Kart CSS sınıfları ve detay URL'si HTML ile doğrulandı mı?
- [ ] Arama ve sayfalama URL'leri doğrulandı mı?
- [ ] Video kaynağı izinli/doğrudan bir bağlantı mı?
- [ ] `.cs3` başarılı derlendi mi, Android TV'de çalıştı mı?

Eklentiyi kullanırken yalnızca erişim ve yayın hakkına sahip olduğunuz kaynakları kullanın.
