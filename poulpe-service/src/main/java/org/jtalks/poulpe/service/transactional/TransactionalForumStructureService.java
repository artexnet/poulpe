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
package org.jtalks.poulpe.service.transactional;

import java.util.List;

import org.jtalks.poulpe.model.dao.BranchDao;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.dao.SectionDao;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.ForumStructureService;

/**
 * @author stanislav bashkirtsev
 * @author Guram Savinov
 */
public class TransactionalForumStructureService implements ForumStructureService {
    private final SectionDao sectionDao;
    private final BranchDao branchDao;
    private final ComponentDao componentDao;

    public TransactionalForumStructureService(SectionDao sectionDao, BranchDao branchDao, ComponentDao componentDao) {
        this.sectionDao = sectionDao;
        this.branchDao = branchDao;
        this.componentDao = componentDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveJcommune(Jcommune jcommune) {
        componentDao.saveOrUpdate(jcommune);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Jcommune getJcommune() {
        return (Jcommune) componentDao.getByType(ComponentType.FORUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBranch(PoulpeBranch branch) {
        PoulpeSection section = (PoulpeSection) branch.getSection();
        section.deleteBranch(branch);
        sectionDao.saveOrUpdate(section);
        branchDao.delete(branch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveBranch(PoulpeBranch branch, PoulpeSection toSection) {
        PoulpeSection fromSection = branch.moveTo(toSection);
        sectionDao.saveOrUpdate(fromSection);
        sectionDao.saveOrUpdate(toSection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Jcommune deleteSectionWithBranches(PoulpeSection section) {
        Jcommune jcommune = (Jcommune) componentDao.getByType(ComponentType.FORUM);
        jcommune.removeSection(section);
        componentDao.saveOrUpdate(jcommune);
        return jcommune;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSectionAndMoveBranches(PoulpeSection toRemove, PoulpeSection toReceiveBranches) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PoulpeBranch saveBranch(PoulpeSection inSection, PoulpeBranch notYetSavedBranch) {
        if (notYetSavedBranch.getSection() == null) {
            inSection.addOrUpdateBranch(notYetSavedBranch);
            notYetSavedBranch.setSection(inSection);
            sectionDao.saveOrUpdate(inSection);
        } else {
            moveBranch(notYetSavedBranch, inSection);
        }
        return notYetSavedBranch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void moveBranch(PoulpeBranch branch, PoulpeBranch target) {
        PoulpeSection targetSection = target.getPoulpeSection();
        List<PoulpeBranch> branches = targetSection.getPoulpeBranches();
        int index = branches.indexOf(target);
        removeBranch(branch);
        targetSection.addBranchIfAbsentTo(index, branch);
        sectionDao.update(targetSection);
    }
}
