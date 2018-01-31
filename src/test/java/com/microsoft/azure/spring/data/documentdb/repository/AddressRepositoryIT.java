/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.domain.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ContactRepositoryConfig.class)
public class AddressRepositoryIT {

    private static final Address TEST_ADDRESS1_PARTITION1 = new Address("111", "111st avenue", "redmond");
    private static final Address TEST_ADDRESS2_PARTITION1 = new Address("222", "98th street", "redmond");
    private static final Address TEST_ADDRESS1_PARTITION2 = new Address("333", "103rd street", "bellevue");
    private static final Address TEST_ADDRESS2_PARTITION2 = new Address("444", "104rd street", "bellevue");

    @Autowired
    AddressRepository repository;

    @Before
    public void setup() {
        repository.save(TEST_ADDRESS1_PARTITION1);
        repository.save(TEST_ADDRESS1_PARTITION2);
        repository.save(TEST_ADDRESS2_PARTITION1);
        repository.save(TEST_ADDRESS2_PARTITION2);
    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void testFindByPartitionedCity() {
        List<Address> result = repository.findByCity("bellevue");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getCity()).isEqualTo("bellevue");
        assertThat(result.get(1).getCity()).isEqualTo("bellevue");
    }

    @Test
    public void testFindAll() {
        final List<Address> result = toList(repository.findAll());

        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void testCountAndDeleteByID() {
        long count = repository.count();
        assertThat(count).isEqualTo(4);

        repository.deleteByStreet(TEST_ADDRESS1_PARTITION1.getPostalCode(), TEST_ADDRESS1_PARTITION1.getCity());

        final List<Address> result = toList(repository.findAll());

        assertThat(result.size()).isEqualTo(3);

        count = repository.count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void testCountAndDeleteEntity() {
        repository.delete(TEST_ADDRESS1_PARTITION1);

        final List<Address> result = toList(repository.findAll());

        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void testUpdateEntity() {
        final Address updatedAddress = new Address(TEST_ADDRESS1_PARTITION1.getPostalCode(), "new street",
                TEST_ADDRESS1_PARTITION1.getCity());

        repository.save(updatedAddress);

        final Address address = repository.findOne(updatedAddress.getPostalCode());

        assertThat(address.getStreet()).isEqualTo(updatedAddress.getStreet());
        assertThat(address.getPostalCode()).isEqualTo(updatedAddress.getPostalCode());
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        if (iterable != null) {
            final List<T> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list;
        }
        return null;
    }
}
