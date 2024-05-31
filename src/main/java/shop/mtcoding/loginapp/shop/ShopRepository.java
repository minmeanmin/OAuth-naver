package shop.mtcoding.loginapp.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Integer> { // JpaRepository를 extends하면 굳이 CRUD 만들 필요가 없다.

}
