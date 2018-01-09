package com.kula.work.service;

import com.kula.work.domain.stock.entity.StockModelEntity;
import com.kula.work.repository.StockModelEntityRepository;
import com.kula.work.service.dto.StockDTO;
import com.kula.work.service.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private StockModelEntityRepository stockModelEntityRepository;
    private StockMapper stockMapper;
    private CacheManager cacheManager;


    public StockService(StockModelEntityRepository stockModelEntityRepository,
                        StockMapper stockMapper,
                        CacheManager cacheManager){
        this.stockMapper = stockMapper;
        this.stockModelEntityRepository = stockModelEntityRepository;
        this.cacheManager = cacheManager;
    }


    public  List<StockDTO> getStockDTOS(){
       return  stockModelEntityRepository.findAll().stream().
            map(stockMapper::stockEntityToStockDTO).collect(Collectors.toList());
    }


    public StockDTO getStock(String code){
        return stockModelEntityRepository.findByCode(code).map(stockMapper::stockEntityToStockDTO).orElse(null);
    }

    public void deleteStock(String code){
         stockModelEntityRepository.findByCode(code).ifPresent(s -> {
             stockModelEntityRepository.delete(s);
             clearCaches(stockMapper.stockEntityToStockDTO(s));

         });
    }

    public StockDTO saveStock(StockDTO stockDTO){

        StockModelEntity stockModelEntity =  stockModelEntityRepository.save(stockMapper.stockDTOToStockModelEntity(stockDTO));
        clearCaches(stockDTO);
        return stockMapper.stockEntityToStockDTO(stockModelEntity);
    }


    public Optional<StockDTO> updateStock(StockDTO stockDTO){
       return Optional.of(stockModelEntityRepository.findOne(stockDTO.getId()))
            .map(stockModelEntity -> {
                 stockModelEntity.setName(stockDTO.getName());
                clearCaches(stockDTO);
                stockModelEntityRepository.save(stockModelEntity);
                return stockModelEntity;
            }).map(StockDTO::new);
    }


    private void clearCaches(StockDTO stockDTO){
        cacheManager.getCache(StockModelEntityRepository.ALL_STOCK_CACHE_NAME).clear();
        cacheManager.getCache(StockModelEntityRepository.STOCK_BY_CODE_CACHE_NAME).evict(stockDTO.getCode());
    }


}
