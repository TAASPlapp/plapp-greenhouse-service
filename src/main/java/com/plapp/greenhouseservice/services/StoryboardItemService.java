package com.plapp.greenhouseservice.services;

import com.plapp.entities.exceptions.ActorNotFoundException;
import com.plapp.greenhouseservice.entities.StoryboardItemDPO;
import com.plapp.greenhouseservice.repositories.StoryboardItemRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoryboardItemService {
    private final StoryboardItemRepository storyboardItemRepository;

    public StoryboardItemDPO findById(long id) {
        return storyboardItemRepository.findById(id).orElse(null);
    }

    public StoryboardItemDPO addStoryboardItem(StoryboardItemDPO item) throws HibernateException,
            ActorNotFoundException {
        if (item.getStoryboard() == null)
            throw new ActorNotFoundException("Storyboard does not exist");

        return storyboardItemRepository.save(item);
    }

    public void removeStoryboardItem(StoryboardItemDPO item) throws HibernateException,
            ActorNotFoundException,
            IllegalArgumentException {

        storyboardItemRepository.delete(item);
    }
}
