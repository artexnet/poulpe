/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.model.dao.hibernate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.poulpe.model.dao.BranchDao;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Kirill Afonin
 * @author Alexey Grigorev
 */
@ContextConfiguration(locations = { "classpath:/org/jtalks/poulpe/model/entity/applicationContext-dao.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BranchHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BranchDao dao;

    private Session session;
    private PoulpeBranch branch;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
    }

    @Test
    public void testSave() {
        givenParentSection();

        dao.saveOrUpdate(branch);

        assertBranchSaved();
    }

    private void givenParentSection() {
        branch = ObjectsFactory.createBranch();
        session.save(branch.getSection());
    }

    private void assertBranchSaved() throws AssertionFailedError {
        assertNotSame(branch.getId(), 0, "Id not created");
        PoulpeBranch actual = retrieveActualBranch();
        actual.setSection(branch.getSection());
        assertReflectionEquals(branch, actual);
    }

    private PoulpeBranch retrieveActualBranch() {
        return ObjectRetriever.retrieveUpdated(branch, session);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testSaveBranchWithNameNotNullViolation() {
        PoulpeBranch branch = new PoulpeBranch();
        dao.saveOrUpdate(branch);
    }

    @Test
    public void testGet() {
        givenBranch();

        PoulpeBranch result = dao.get(branch.getId());

        assertNotNull(result);
        assertEquals(result.getId(), branch.getId());
    }

    private void givenBranch() {
        givenParentSection();
        session.save(branch);
    }

    @Test
    public void testGetInvalidId() {
        PoulpeBranch result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        givenBranch();

        String newName = "new name";
        branch.setName(newName);

        dao.saveOrUpdate(branch);

        assertNameChanged(newName);
    }

    private void assertNameChanged(String newName) {
        PoulpeBranch result = retrieveActualBranch();
        assertEquals(result.getName(), newName);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class)
    public void testUpdateNotNullViolation() {
        givenBranch();
        branch.setName(null);

        dao.saveOrUpdate(branch);
    }

    @Test
    public void testDelete() {
        givenBranch();

        boolean result = dao.delete(branch.getId());
        assertTrue(result, "Entity is not deleted");

        assertBranchDeleted();
    }

    private void assertBranchDeleted() {
        int branchCount = retrieveActualBranchesAmount();
        assertEquals(branchCount, 0);
    }

    private int retrieveActualBranchesAmount() {
        String countQuery = "select count(*) from PoulpeBranch";
        Number count = (Number) session.createQuery(countQuery).uniqueResult();
        return count.intValue();
    }

    @Test
    public void testDeleteInvalidId() {
        boolean result = dao.delete(-100500L);

        assertFalse(result, "Entity deleted");
    }

    @Test
    public void testGetAll() {
        givenTwoBranches();

        List<PoulpeBranch> branches = dao.getAll();

        assertEquals(branches.size(), 2);
    }

    private void givenTwoBranches() {
        addBranch();
        addBranch();
    }

    private void addBranch() {
        PoulpeBranch randomBranch = ObjectsFactory.createBranch();
        session.save(randomBranch.getSection());
        session.save(randomBranch);
    }

    @Test
    public void testGetAllWithEmptyTable() {
        List<PoulpeBranch> branches = dao.getAll();

        assertTrue(branches.isEmpty());
    }

    @Test
    public void testIsExist() {
        givenBranch();

        assertTrue(dao.isExist(branch.getId()));
    }

    @Test
    public void testIsNotExist() {
        assertFalse(dao.isExist(99999L));
    }

    /**
     * Tests if Hibernate mapping allows to save branches adding them to the
     * section.
     */
    @Test
    public void branchSectionBidirectionalTest() {
        PoulpeBranch branch = ObjectsFactory.createBranch();
        PoulpeSection section = ObjectsFactory.createSection();
        session.save(section);

        section = (PoulpeSection) session.load(PoulpeSection.class, section.getId());
        branch.setSection(section);
        section.addOrUpdateBranch(branch);
        session.save(branch);

        assertTrue(branch.getId() != 0);

        section = (PoulpeSection) session.load(PoulpeSection.class, section.getId());
        assertEquals(section.getBranches().size(), 1);
        assertReflectionEquals(branch, section.getBranches().get(0));
    }

}
