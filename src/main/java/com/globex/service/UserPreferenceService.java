package com.globex.service;

import com.globex.model.UserPreferences;
import com.globex.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    public void updateUserPreferences(Long userId, String newPreference) {
        Optional<UserPreferences> optionalPreferences = userPreferencesRepository.findByUserId(userId);
        UserPreferences userPreferences;

        if (optionalPreferences.isPresent()) {
            userPreferences = optionalPreferences.get();
            String existingPreferences = userPreferences.getPreferences();
            if (existingPreferences == null || existingPreferences.isEmpty()) {
                userPreferences.setPreferences(newPreference);
            } else if (!existingPreferences.contains(newPreference)) {
                userPreferences.setPreferences(existingPreferences + "," + newPreference);
            }
        } else {
            userPreferences = new UserPreferences();
            userPreferences.setUserId(userId);
            userPreferences.setPreferences(newPreference);
        }

        userPreferencesRepository.save(userPreferences);
    }

    public String getUserPreferences(Long userId) {
        Optional<UserPreferences> optionalPreferences = userPreferencesRepository.findByUserId(userId);
        return optionalPreferences.map(UserPreferences::getPreferences).orElse("");
    }
}
