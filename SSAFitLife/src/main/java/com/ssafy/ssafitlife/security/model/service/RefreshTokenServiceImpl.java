package com.ssafy.ssafitlife.security.model.service;

import com.ssafy.ssafitlife.security.model.dao.RefreshTokenDao;
import com.ssafy.ssafitlife.security.model.dto.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenDao refreshTokenDao;

    public RefreshTokenServiceImpl(RefreshTokenDao refreshTokenDao) {
        this.refreshTokenDao = refreshTokenDao;
    }

    @Override
    public boolean isRefreshTokenExists(String refreshToken) {
        return refreshTokenDao.existsByRefresh(refreshToken);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        refreshTokenDao.deleteByRefresh(refreshToken);
    }

    @Override
    public void saveToken(RefreshToken refreshToken) {
        refreshTokenDao.insertToken(refreshToken);
    }
}