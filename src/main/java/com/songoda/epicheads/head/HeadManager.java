package com.songoda.epicheads.head;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeadManager {
    private final Set<Head> registeredHeads = new HashSet<>();
    private final List<Head> localRegisteredHeads = new ArrayList<>();
    private final List<Category> registeredCategories = new ArrayList<>();
    private final Set<Head> disabledHeads = new HashSet<>();

    // Cache for getHeads() result
    private List<Head> cachedHeads = null;

    // Invalidate cache whenever underlying collections change.
    private void invalidateCache() {
        cachedHeads = null;
    }

    public Head addHead(Head head) {
        this.registeredHeads.add(head);
        invalidateCache();
        return head;
    }

    public void addHeads(Head... heads) {
        this.registeredHeads.addAll(Arrays.asList(heads));
        invalidateCache();
    }

    public void addHeads(Collection<Head> heads) {
        this.registeredHeads.addAll(heads);
        invalidateCache();
    }

    public void addLocalHeads(Head... heads) {
        this.localRegisteredHeads.addAll(Arrays.asList(heads));
        invalidateCache();
    }

    public void addLocalHead(Head head) {
        this.localRegisteredHeads.add(head);
        invalidateCache();
    }

    public void addLocalHeads(Collection<Head> heads) {
        this.localRegisteredHeads.addAll(heads);
        invalidateCache();
    }

    public Head getHead(String name) {
        return getHeads().stream()
                .filter(head -> head.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public List<Head> getHeadsByQuery(String query) {
        List<Head> result = getHeads().stream()
                .filter(head -> head.getName().contains(query))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            for (Category category : this.registeredCategories) {
                if (!category.getName().equalsIgnoreCase(query)) {
                    continue;
                }
                return getHeads().stream()
                        .filter(head -> head.getCategory() == category)
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    public List<Head> getHeadsByCategory(Category category) {
        List<Head> list = new ArrayList<>();
        for (Head head : getHeads()) {
            if (head.getCategory().equals(category)) {
                list.add(head);
            }
        }
        return list;
    }

    // Cached getHeads() method
    public List<Head> getHeads() {
        if (cachedHeads == null) {
            cachedHeads = Collections.unmodifiableList(Stream.concat(
                            this.registeredHeads.stream(),
                            this.localRegisteredHeads.stream())
                    .sorted(Comparator.comparing(Head::getName))
                    .collect(Collectors.toList()));
        }
        return cachedHeads;
    }

    public Integer getNextLocalId() {
        if (this.localRegisteredHeads.isEmpty()) {
            return 1;
        }
        return this.localRegisteredHeads.get(this.localRegisteredHeads.size() - 1).getId() + 1;
    }

    public List<Head> getLocalHeads() {
        return Collections.unmodifiableList(this.localRegisteredHeads);
    }

    public List<Head> getGlobalHeads() {
        return new ArrayList<>(this.registeredHeads);
    }

    public Head disableHead(Head head) {
        if (head.isLocal() && this.localRegisteredHeads.remove(head)) {
            invalidateCache();
            return head;
        }
        this.disabledHeads.add(head);
        this.registeredHeads.remove(head);
        invalidateCache();
        return head;
    }

    public Set<Head> getDisabledHeads() {
        return Collections.unmodifiableSet(this.disabledHeads);
    }

    public void removeLocalHead(Head head) {
        this.localRegisteredHeads.remove(head);
        invalidateCache();
    }

    public Category addCategory(Category category) {
        if (!this.registeredCategories.contains(category)) {
            this.registeredCategories.add(category);
        }
        return category;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(this.registeredCategories);
    }

    public Category getCategory(String name) {
        for (Category category : this.registeredCategories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

    public Category getOrCreateCategoryByName(String name) {
        for (Category category : this.registeredCategories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return addCategory(new Category(name));
    }

    public void clear() {
        this.registeredHeads.clear();
        this.localRegisteredHeads.clear();
        this.disabledHeads.clear();
        this.registeredCategories.clear();
        invalidateCache();
    }
}