package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.greenhouseservice.entities.StoryboardDPO;
import com.plapp.greenhouseservice.repositories.StoryboardRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryboardService {
    private final StoryboardRepository storyboardRepository;

    public StoryboardDPO findById(long id) {
        return storyboardRepository.findById(id).orElse(null);
    }

    public List<StoryboardDPO> getAllStoryboards() {
        return storyboardRepository.findAll();
    }

    public StoryboardDPO createStoryboard(StoryboardDPO storyboard) throws HibernateException {
        return storyboardRepository.save(storyboard);
    }

    public StoryboardDPO updateStoryboard(StoryboardDPO storyboard) throws ActorNotFoundException,
                                                                           HibernateException {
        StoryboardDPO oldStoryboard = storyboardRepository.findById(storyboard.getId()).orElse(null);
        if (oldStoryboard == null)
            throw new ActorNotFoundException("Storyboard does not exist");

        if (storyboard.getSummary() != null)
            oldStoryboard.setSummary(storyboard.getSummary());

        return this.createStoryboard(oldStoryboard);
    }

    public void removeStoryboard(long storyboardId) throws HibernateException,
                                                           ActorNotFoundException {
        if (!storyboardRepository.existsById(storyboardId))
            throw new ActorNotFoundException("Storyboard does not exists");
        storyboardRepository.deleteById(storyboardId);
    }
}
