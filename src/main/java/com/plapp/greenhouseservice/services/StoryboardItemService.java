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

        //List<StoryboardItemDPO> items = storyboard.getStoryboardItems();
        //if (items == null || items.size() < 1)
        //    items = new ArrayList<>();
        //items.add(item);

        return storyboardItemRepository.save(item);
    }

    public void removeStoryboardItem(StoryboardItemDPO item) throws HibernateException,
            ActorNotFoundException,
            IllegalArgumentException {
        /*StoryboardDPO storyboard = storyboardRepository.findById(storyboardId).orElse(null);
        if (storyboard == null)
            throw new ActorNotFoundException("Storyboard does not exist");

        List<StoryboardItemDPO> items = storyboard.getStoryboardItems();
        if (items == null || items.size() < 1)
            throw new IllegalArgumentException("Storyboard does not have any item");

        List<StoryboardItemDPO> matching = Lists.filter(items, i -> i.getId() == itemId);
        if (matching.size() < 1)
            throw new ActorNotFoundException("Storyboard does not contain the specified item");

        items.remove(matching.get(0));
        storyboard.setStoryboardItems(items);

        return this.createOrUpdateStoryboard(storyboard);*/
        storyboardItemRepository.delete(item);
    }
}
